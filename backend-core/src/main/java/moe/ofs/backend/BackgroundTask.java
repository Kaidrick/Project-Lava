package moe.ofs.backend;

import moe.ofs.backend.box.BoxOfFlyableUnit;
import moe.ofs.backend.box.BoxOfParking;
import moe.ofs.backend.dataset.ExportUnitDataSet;
import moe.ofs.backend.function.RadioCommands;
import moe.ofs.backend.handlers.*;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.NewExportPollingHanlder;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.ServerPollingHandler;
import moe.ofs.backend.request.server.ServerFillerRequest;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static moe.ofs.backend.ControlPanelApplication.logController;

public class BackgroundTask {
    private static final RequestHandler<BaseRequest> requestHandler = RequestHandler.getInstance();
//    private static final ExportPollingHandler exportPollingHandler = ExportPollingHandler.getInstance();
    private static final ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

    private static final NewExportPollingHanlder newExportPollingHanlder = new NewExportPollingHanlder();

    private static final ExportUnitDataSet exportUnitDataSet = ControlPanelApplication.applicationContext
            .getBean("exportUnitDataSet", ExportUnitDataSet.class);

    private static ScheduledExecutorService mainRequestScheduler;
    private static ScheduledExecutorService exportPollingScheduler;
    private static ScheduledExecutorService serverPollingScheduler;

    public static ConfigurableApplicationContext applicationContext;

    public static boolean needRestart;
    public static boolean stopSign;

    public static AtomicBoolean isHalted = new AtomicBoolean(false);


    private static void shutdownExecutorService(ExecutorService service) {
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

    public static void halt() throws InterruptedException {
        if(isHalted.get()) return;

        backgroundThread.interrupt();
        requestHandler.dispose();

        shutdownExecutorService(mainRequestScheduler);
        shutdownExecutorService(serverPollingScheduler);
        shutdownExecutorService(exportPollingScheduler);

        isHalted.set(true);
    }


    public static Runnable background = () -> {
        try {
            startBackgroundTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public static Thread backgroundThread = new Thread(background);


    public static Thread heartbeatSignalThread;


    private static boolean initialized;
    private static void initCore() throws IOException {
        if(!initialized) {
            RadioCommands.init();
            Plugin.loadPlugins();
            logController.populateLoadedPluginListView();


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
            System.out.println("Already Initialized in last session");
        }
    }

    // restart background task when connect is cut
    public static void startBackgroundTask() throws IOException {

        BackgroundTaskRestartObservable.invokeAll();

        initCore();

        BoxOfParking.init();
        BoxOfFlyableUnit.init();
//        BoxOfExportUnit.init();
        exportUnitDataSet.init();

        isHalted.set(false);

        System.out.println("Starting background tasks");

        ConnectionManager.sanitizeDataPipeline(requestHandler);

//        exportPollingHandler.init();
        newExportPollingHanlder.init();
        serverPollingHandler.init();

        MissionStartObservable.invokeAll();


//        Runnable exportPolling = exportPollingHandler::poll;
        Runnable exportPolling = newExportPollingHanlder::poll;

        Runnable serverPolling = () -> {
            try {
                serverPollingHandler.poll();
//              throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // main loop
//         requests are sent and result are received in this thread only
        Runnable mainLoop = () -> {
            new ServerFillerRequest() {
                { handle = Handle.EMPTY; port = 3010; }
            }.send();
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
                0, 1, TimeUnit.MILLISECONDS);

        serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                0, 1, TimeUnit.MILLISECONDS);
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
