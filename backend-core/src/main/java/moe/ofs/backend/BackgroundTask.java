package moe.ofs.backend;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.dispatcher.model.LavaTask;
import moe.ofs.backend.dispatcher.services.LavaTaskDispatcher;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.handlers.BackgroundTaskRestartObservable;
import moe.ofs.backend.handlers.LuaScriptInjectionObservable;
import moe.ofs.backend.handlers.MissionStartObservable;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.handlers.starter.services.LuaScriptInjectService;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.message.ConnectionStatusChange;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.message.connection.ConnectionStatus;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.PollHandlerService;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.request.services.RequestTransmissionService;
import moe.ofs.backend.services.*;
import moe.ofs.backend.telemetry.serivces.LuaStateTelemetryService;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class BackgroundTask {

    private final RequestHandler requestHandler;
    private final ConnectionManager connectionManager;

    private boolean started;

    private final PollHandlerService exportObjectPollService;

    private final PollHandlerService playerInfoPollService;

    private final LuaStateTelemetryService luaStateTelemetryService;

    private final LavaTaskDispatcher lavaTaskDispatcher;

    private final LuaScriptInjectService luaScriptInjectService;

    private final List<LuaScriptStarter> luaScriptStarters;

    private final List<Plugin> plugins;

    private final List<MissionPersistenceRepository> repositoryList;

    private final List<StaticService> staticServices;

    private final Sender sender;

    private OperationPhase phase;

    private final RequestTransmissionService requestTransmissionService;

    private String taskDcsMapTheaterName;
    private String dcsApplicationVersion;

    // TODO: if the background task is halted, this field should be reset to null
    public String getTaskDcsMapTheaterName() {
        return taskDcsMapTheaterName;
    }

    private void setDcsApplicationVersion(String version) {
        dcsApplicationVersion = version;
    }

    public String getDcsApplicationVersion() {
        return dcsApplicationVersion;
    }

    public OperationPhase getPhase() {
        return phase;
    }

    @PostConstruct
    private void loadPlugins() {
        log.info("************ Loading Plugins *************");
        plugins.forEach(plugin -> System.out.println("plugin = " + plugin.getName()));
        Plugin.loadedPlugins.addAll(plugins);
        Plugin.loadedPlugins.forEach(Plugin::load);  // FIXME: this is so bad
    }


    @Autowired
    public BackgroundTask(

            RequestHandler requestHandler, ConnectionManager connectionManager,
            @Qualifier("exportObjectDelta")
                    PollHandlerService exportObjectPollService,
            @Qualifier("playerInfoBulk")
                    PollHandlerService playerInfoPollService,

            LuaStateTelemetryService luaStateTelemetryService, LavaTaskDispatcher lavaTaskDispatcher,
            LuaScriptInjectService luaScriptInjectService, List<LuaScriptStarter> luaScriptStarters,
            List<Plugin> plugins,
            List<MissionPersistenceRepository> repositoryList, List<StaticService> staticServices,
            Sender sender, RequestTransmissionService requestTransmissionService) {
        this.requestHandler = requestHandler;
        this.connectionManager = connectionManager;

        this.luaStateTelemetryService = luaStateTelemetryService;

        this.exportObjectPollService = exportObjectPollService;
        this.playerInfoPollService = playerInfoPollService;

        this.lavaTaskDispatcher = lavaTaskDispatcher;

        this.luaScriptInjectService = luaScriptInjectService;
        this.luaScriptStarters = luaScriptStarters;

        this.plugins = plugins;
        this.repositoryList = repositoryList;
        this.staticServices = staticServices;

        // JMS
        this.sender = sender;

        // Request output
        this.requestTransmissionService = requestTransmissionService;

        currentTask = this;
        injectionTaskChecks = new HashMap<>();

        phase = OperationPhase.PREPARING;
    }

    private ScheduledExecutorService mainRequestScheduler;
    private ScheduledExecutorService exportPollingScheduler;
    private ScheduledExecutorService serverPollingScheduler;

    private static BackgroundTask currentTask;

    public static BackgroundTask getCurrentTask() {
        return currentTask;
    }

    private static Instant taskStartTime;

    public static Instant getTaskStartTime() {
        return taskStartTime;
    }

    private static Map<ScriptInjectionTask, Boolean> injectionTaskChecks;

    public Map<ScriptInjectionTask, Boolean> getInjectionTaskChecks() {
        return new HashMap<>(injectionTaskChecks);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        // set value anyway
        this.started = started;

        if(started) {

            log.info("Starting Background Task...");

            taskStartTime = Instant.now();

            // there can only be one background thread
            // how to ensure this is a singleton?
            if(backgroundThread == null) {
                backgroundThread = new Thread(background);
                backgroundThread.setName("bg task");
                backgroundThread.start();
            } else {
                log.error("Duplicate background thread");
            }

        } else {
            log.info("Stopping Background Task...");
            phase = OperationPhase.STOPPING;

            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                backgroundThread.interrupt();
                backgroundThread = null;

                phase = OperationPhase.IDLE;
            }
        }
    }

    /**
     * Listen for RequestHandler connection status change. If status changes to CONNECTED, start background task.
     * @param textMessage JSON string that represents the change.
     * @throws JMSException if the message cannot be parsed or converted correctly.
     */
    @JmsListener(destination = "dcs.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'change'")
    public void connectionStatusChangeListener(TextMessage textMessage) throws JMSException {
        Gson gson = new Gson();
        if (textMessage != null && textMessage.getText() != null) {
            ConnectionStatusChange change = gson.fromJson(textMessage.getText(), ConnectionStatusChange.class);
            setStarted(change.getStatus() == ConnectionStatus.CONNECTED);
            log.info("Connection Status Change: {} at {}", change.getStatus(), change.getTimestamp().getEpochSecond());
        } else {
            log.warn("what is this a null message?");
        }

    }

    public AtomicBoolean isHalted = new AtomicBoolean(false);


    private void shutdownExecutorService(ExecutorService service) {
        if(service != null) {
            service.shutdown();
            try {
                if (!service.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                service.shutdownNow();
            }
        }
    }

    // probably should use a property changed handler? so that request handler don't need a backgrountask instance
    public void stop() throws InterruptedException {
        if(isHalted.get()) {
            return;
        }

        if(backgroundThread != null)
            backgroundThread.interrupt();

        requestHandler.dispose();

        shutdownExecutorService(mainRequestScheduler);
        shutdownExecutorService(serverPollingScheduler);
        shutdownExecutorService(exportPollingScheduler);

        lavaTaskDispatcher.haltAll();

        isHalted.set(true);
    }


    private final Runnable background = () -> {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public Thread backgroundThread;

    // restart background task when connect is cut
    public void start() throws IOException {

        sender.send("BackgroundTaskRestarting");

        phase = OperationPhase.LOADING;

        BackgroundTaskRestartObservable.invokeAll();

        connectionManager.sanitizeDataPipeline();

        log.info("Initializing data services");

        repositoryList.stream().peek(System.out::println).forEach(MissionPersistenceRepository::dispose);
        staticServices.forEach(StaticService::loadData);


        isHalted.set(false);

        log.info("Starting background tasks and services");

        exportObjectPollService.init();
        playerInfoPollService.init();

        // on exception, do clean up
        // close all connections immediately
        //
        Runnable exportPolling = () -> {
            try {
                exportObjectPollService.poll();
            } catch (IOException e) {
//                e.printStackTrace();

                requestHandler.setTrouble(true);

                // shutdown
                requestHandler.shutdownConnections();
            }
        };

        Runnable serverPolling = () -> {
            try {
                playerInfoPollService.poll();
            } catch (IOException e) {
//                e.printStackTrace();

                requestHandler.setTrouble(true);

                // shutdown
                requestHandler.shutdownConnections();
            }
        };

        // main loop
//         requests are sent and result are received in this thread only
        Runnable mainLoop = () -> {
//            log.info("main loop -> " + Thread.currentThread().getName());
//            if(requestHandler.hasPendingServerRequest())
            requestTransmissionService.send(new FillerRequest(Level.SERVER));
            requestTransmissionService.send(new FillerRequest(Level.EXPORT));

            try {
//                requestHandler.transmitAndReceive();
                requestHandler.transmissionCycle();
            } catch (Exception e) {
                e.printStackTrace();

                // shutdown
//                requestHandler.shutdownConnections();
            }
        };

        requestHandler.createConnections();

//         dedicated polling thread
//         polling and receive data only in this thread
        mainRequestScheduler = Executors.newSingleThreadScheduledExecutor();
        mainRequestScheduler.scheduleWithFixedDelay(mainLoop, 0, 1, TimeUnit.MILLISECONDS);

        exportPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        exportPollingScheduler.scheduleWithFixedDelay(exportPolling,
                0, 100, TimeUnit.MILLISECONDS);
//
        serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                0, 100, TimeUnit.MILLISECONDS);


        // check in lua mission env for global variable persistent initialization
        // this flag can only be reset by mission restart event handler

        // lua must return a string
        boolean flag = ((ServerDataRequest) requestTransmissionService.send(
                new ServerDataRequest("return tostring(lava_mission_persistent_initialization)")))
                .getAsBoolean();

        LuaScripts.request(LuaQueryEnv.SERVER_CONTROL, "return _ED_VERSION")
                .addProcessable(this::setDcsApplicationVersion);

        luaScriptStarters.stream()
                .map(LuaScriptStarter::injectScript)
                .forEach(luaScriptInjectService::add);

        // put inject result into map that can be referenced from other sources
        injectionTaskChecks.putAll(luaScriptInjectService.invokeInjection());
//                .forEach(((task, aBoolean) -> System.out.println(task.getScriptIdentName() + " -> " + aBoolean)));

        if(!flag) {
            log.info("injecting mission persistence");

            LuaScriptInjectionObservable.invokeAll();

            requestTransmissionService
                    .send(new ServerDataRequest("lava_mission_persistent_initialization = true"));

            log.info("mission persistence initialized");
        } else {

            log.info("persistence already initialized");

        }

        //         initialize plugins
        Plugin.loadedPlugins.forEach(Plugin::init);

        log.info("Schedulers running, background task ready, mission data initialized");

        requestTransmissionService.send(
                // also get dcs version here
                new ServerDataRequest("return env.mission.theatre")
                        .addProcessable(theater -> {
                            MissionStartObservable.invokeAll(theater);
                            log.info("Mission started in " + theater);

                            taskDcsMapTheaterName = theater;  // store theater name
                        })
        );

        phase = OperationPhase.RUNNING;

        // initializing task dispatcher
        lavaTaskDispatcher.init();
        LavaTask telemetryTask =
                new LavaTask("Lua State Telemetry Task", luaStateTelemetryService::recordTelemetry, 5000);
//        testTask.setStopCondition(t -> t.getCycles().get() > 5);  // test stop condition

        lavaTaskDispatcher.addTask(telemetryTask);

        log.info("Background Task start completed");
    }

    interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    public static void processResource(URI uri, IOConsumer<Path> action) throws IOException {
        try {
            Path p = Paths.get(uri);
            action.accept(p);
        }
        catch(FileSystemNotFoundException ex) {
            try(FileSystem fs = FileSystems.newFileSystem(
                    uri, Collections.<String, Object>emptyMap())) {
                Path p = fs.provider().getPath(uri);
                action.accept(p);
            }
        }
    }
}
