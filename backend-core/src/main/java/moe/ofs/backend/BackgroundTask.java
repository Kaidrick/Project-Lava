package moe.ofs.backend;

import moe.ofs.backend.function.RadioCommands;
import moe.ofs.backend.handlers.*;
import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.ExportPollHandler;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.ServerPollHandler;
import moe.ofs.backend.request.server.ServerFillerRequest;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.ParkingInfoService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.services.jpa.ExportObjectJpaService;
import moe.ofs.backend.services.jpa.PlayerInfoJpaService;
import moe.ofs.backend.services.map.FlyableUnitMapService;
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

    private static final ExportPollHandler exportPollHandler =
            ControlPanelApplication.applicationContext.getBean(ExportPollHandler.class);
    private static final ServerPollHandler serverPollHandler =
            ControlPanelApplication.applicationContext.getBean(ServerPollHandler.class);

    private static final ExportObjectService EXPORT_OBJECT_SERVICE =
            ControlPanelApplication.applicationContext.getBean(ExportObjectJpaService.class);

    private static final PlayerInfoService PLAYER_INFO_SERVICE =
            ControlPanelApplication.applicationContext.getBean(PlayerInfoJpaService.class);

    private static final FlyableUnitService FLYABLE_UNIT_SERVICE =
            ControlPanelApplication.applicationContext.getBean(FlyableUnitMapService.class);

    private static final ParkingInfoService PARKING_INFO_SERVICE =
            ControlPanelApplication.applicationContext.getBean(ParkingInfoService.class);


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

        // dispose obsolete data if any
        FLYABLE_UNIT_SERVICE.dispose();
        PARKING_INFO_SERVICE.dispose();
        EXPORT_OBJECT_SERVICE.dispose();
        PLAYER_INFO_SERVICE.dispose();

        // load static data
        FLYABLE_UNIT_SERVICE.loadData();
        PARKING_INFO_SERVICE.loadData();

        isHalted.set(false);

        System.out.println("Starting background tasks");

        ConnectionManager.sanitizeDataPipeline(requestHandler);


        exportPollHandler.init();
        serverPollHandler.init();

        MissionStartObservable.invokeAll();

        Runnable exportPolling = exportPollHandler::poll;

        Runnable serverPolling = () -> {
            try {
                serverPollHandler.poll();
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
