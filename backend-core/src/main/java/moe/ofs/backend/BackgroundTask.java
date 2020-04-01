package moe.ofs.backend;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.handlers.*;
import moe.ofs.backend.logmanager.Logger;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.PollHandlerService;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.server.ServerDataRequest;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.ParkingInfoService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class BackgroundTask implements PropertyChangeListener {

    private final RequestHandler requestHandler = RequestHandler.getInstance();

    private boolean started;

    private PropertyChangeSupport support;

    private final PollHandlerService exportObjectPollService;

    private final PollHandlerService playerInfoPollService;

    private final ExportObjectService exportObjectService;

    private final PlayerInfoService playerInfoService;

    private final FlyableUnitService flyableUnitService;

    private final ParkingInfoService parkingInfoService;

    private final List<Plugin> plugins;


    @PostConstruct
    private void loadPlugins() {
        Plugin.loadedPlugins.addAll(plugins);
        Plugin.loadedPlugins.forEach(p -> System.out.println("Loaded plugin -> " + p.getName()));

        // how to add to obserable list?
    }


    @Autowired
    public BackgroundTask(

            @Qualifier("exportObjectDelta") PollHandlerService exportObjectPollService,
            @Qualifier("playerInfoBulk") PollHandlerService playerInfoPollService,

            ExportObjectService exportObjectService,
            PlayerInfoService playerInfoService,
            FlyableUnitService flyableUnitService,
            ParkingInfoService parkingInfoService, List<Plugin> plugins) {

        this.exportObjectPollService = exportObjectPollService;
        this.playerInfoPollService = playerInfoPollService;
        this.exportObjectService = exportObjectService;
        this.playerInfoService = playerInfoService;

        this.flyableUnitService = flyableUnitService;
        this.parkingInfoService = parkingInfoService;

        this.plugins = plugins;

        currentTask = this;
        support = new PropertyChangeSupport(this);

        // listen to property change of "trouble"
        requestHandler.addPropertyChangeListener(this);
    }

    private ScheduledExecutorService mainRequestScheduler;
    private ScheduledExecutorService exportPollingScheduler;
    private ScheduledExecutorService serverPollingScheduler;

    private static BackgroundTask currentTask;

    public static BackgroundTask getCurrentTask() {
        return currentTask;
    }

    // property change listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {

        // fire property change iff value changed
        if(this.started != started)
            support.firePropertyChange("started", this.started, started);

        // set value anyway
        this.started = started;

        if(started) {
            log.info("Starting Background Task...");
            backgroundThread = new Thread(background);
            backgroundThread.setName("bg task");
            backgroundThread.start();
        } else {
            log.info("Stopping Background Task...");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("trouble")) {

            if((boolean) propertyChangeEvent.getNewValue()) {
                // trouble, stop background task
                setStarted(false);
            } else {
                // no trouble, start background task
                setStarted(true);
            }
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
        if(isHalted.get()) return;

        backgroundThread.interrupt();
        requestHandler.dispose();

        shutdownExecutorService(mainRequestScheduler);
        shutdownExecutorService(serverPollingScheduler);
        shutdownExecutorService(exportPollingScheduler);

        isHalted.set(true);
    }


    private final Runnable background = () -> {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public Thread backgroundThread = new Thread(background);

    private boolean initialized;

    private void initCore() throws IOException {
        if(!initialized) {

            // TODO --> VERY BAD IMPLEMENTATION! REFACTOR!
            PlayerEnterServerObservable playerEnterServerObservable =
                    playerInfo -> Logger.log("New connection: " + playerInfo.getName()
                            + "@" + playerInfo.getIpaddr());
            playerEnterServerObservable.register();

            PlayerLeaveServerObservable playerLeaveServerObservable =
                    playerInfo -> Logger.log("Player left: " + playerInfo.getName()
                            + "@" + playerInfo.getIpaddr());
            playerLeaveServerObservable.register();

            PlayerSlotChangeObservable playerSlotChangeObservable =
                    (previous, current) -> Logger.log(
                            current.getName()
                                    + " slot change: " + previous.getSlot() + " -> " + current.getSlot());
            playerSlotChangeObservable.register();

            ExportUnitSpawnObservable exportUnitSpawnObservable =
                    unit -> Logger.log(String.format("Unit Spawn: %s (RuntimeID: %s) - %s Type",
                            unit.getUnitName(), unit.getRuntimeID(), unit.getName()));
            exportUnitSpawnObservable.register();

            ExportUnitDespawnObservable exportUnitDespawnObservable =
                    unit -> Logger.log(String.format("Unit Despawn: %s (RuntimeID: %s) - %s Type",
                            unit.getUnitName(), unit.getRuntimeID(), unit.getName()));
            exportUnitDespawnObservable.register();

            initialized = true;
        } else {
            log.info("TODO --> Already Initialized in last session");
        }
    }

    // restart background task when connect is cut
    public void start() throws IOException {


        BackgroundTaskRestartObservable.invokeAll();

        ConnectionManager.sanitizeDataPipeline();

        initCore();

        log.info("Initializing data services");
        // dispose obsolete data if any
        flyableUnitService.dispose();
        parkingInfoService.dispose();
        exportObjectService.dispose();
        playerInfoService.dispose();

        // load static data
        flyableUnitService.loadData();
        parkingInfoService.loadData();

        isHalted.set(false);

        log.info("Starting background tasks and services");

        exportObjectPollService.init();
        playerInfoPollService.init();

        Runnable exportPolling = () -> {
            try {
                exportObjectPollService.poll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable serverPolling = () -> {
            try {
                playerInfoPollService.poll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // main loop
//         requests are sent and result are received in this thread only
        Runnable mainLoop = () -> {
//            log.info("main loop -> " + Thread.currentThread().getName());
            if(requestHandler.hasPendingServerRequest())
                new FillerRequest(Level.SERVER).send();

            if(requestHandler.hasPendingExportRequest())
                new FillerRequest(Level.EXPORT).send();

            try {
                requestHandler.transmitAndReceive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

//         dedicated polling thread
//         polling and receive data only in this thread
        mainRequestScheduler = Executors.newSingleThreadScheduledExecutor();
        mainRequestScheduler.scheduleWithFixedDelay(mainLoop, 0, 1, TimeUnit.MILLISECONDS);

        exportPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        exportPollingScheduler.scheduleWithFixedDelay(exportPolling,
                0, 200, TimeUnit.MILLISECONDS);

        serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                0, 200, TimeUnit.MILLISECONDS);

        // initialize plugins
        Plugin.loadedPlugins.forEach(Plugin::init);

        log.info("Schedulers running, background task ready, mission data initialized");

        log.info("((ServerDataRequest) new ServerDataRequest(\"return os.time()\").send()).getAsInt() = " + ((ServerDataRequest) new ServerDataRequest("return os.time()").send()).getAsInt());

        new ServerDataRequest("return env.mission.theatre")
                .addProcessable(theater -> {
                    MissionStartObservable.invokeAll(theater);
                    log.info("Mission started in " + theater);
                }).send();
    }

    interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    public static void processResource(URI uri, IOConsumer<Path> action) throws IOException {
        try {
            Path p= Paths.get(uri);
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
