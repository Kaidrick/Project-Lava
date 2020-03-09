package moe.ofs.backend;

import moe.ofs.backend.handlers.*;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.Level;
import moe.ofs.backend.request.PollHandlerService;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.services.ExportObjectService;
import moe.ofs.backend.services.FlyableUnitService;
import moe.ofs.backend.services.ParkingInfoService;
import moe.ofs.backend.services.PlayerInfoService;
import moe.ofs.backend.util.ConnectionManager;
import moe.ofs.backend.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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

@Component
public class BackgroundTask {

    private final RequestHandler requestHandler = RequestHandler.getInstance();

    private final PollHandlerService exportObjectPollService;

    private final PollHandlerService playerInfoPollService;

    private final ExportObjectService exportObjectService;

    private final PlayerInfoService playerInfoService;

    private final FlyableUnitService flyableUnitService;

    private final ParkingInfoService parkingInfoService;

    @Autowired
    public BackgroundTask(

            @Qualifier("exportObject") PollHandlerService exportObjectPollService,
            @Qualifier("playerInfo") PollHandlerService playerInfoPollService,

            ExportObjectService exportObjectService,
            PlayerInfoService playerInfoService,
            FlyableUnitService flyableUnitService,
            ParkingInfoService parkingInfoService) {

        this.exportObjectPollService = exportObjectPollService;
        this.playerInfoPollService = playerInfoPollService;
        this.exportObjectService = exportObjectService;
        this.playerInfoService = playerInfoService;

        this.flyableUnitService = flyableUnitService;
        this.parkingInfoService = parkingInfoService;

        currentTask = this;
    }

    private ScheduledExecutorService mainRequestScheduler;
    private ScheduledExecutorService exportPollingScheduler;
    private ScheduledExecutorService serverPollingScheduler;

    public static boolean stop;

    private static BackgroundTask currentTask;

    public static boolean isStop() {
        return stop;
    }

    public static void setStop(boolean stop) {
        BackgroundTask.stop = stop;

        if(stop) {
            try {
                currentTask.halt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean stopSign;

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
    public void halt() throws InterruptedException {
        if(isHalted.get()) return;

        backgroundThread.interrupt();
        requestHandler.dispose();

        shutdownExecutorService(mainRequestScheduler);
        shutdownExecutorService(serverPollingScheduler);
        shutdownExecutorService(exportPollingScheduler);

        isHalted.set(true);
    }


    public Runnable background = () -> {
        try {
            startBackgroundTask();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    public Thread backgroundThread = new Thread(background);


    public Thread heartbeatSignalThread;


    private boolean initialized;
    private void initCore() throws IOException {
        if(!initialized) {

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
    public void startBackgroundTask() throws IOException {

        setStop(false);

        BackgroundTaskRestartObservable.invokeAll();

        ConnectionManager.sanitizeDataPipeline(requestHandler);

        initCore();

        // dispose obsolete data if any
        flyableUnitService.dispose();
        parkingInfoService.dispose();
        exportObjectService.dispose();
        playerInfoService.dispose();

        // load static data
        flyableUnitService.loadData();
        parkingInfoService.loadData();

        isHalted.set(false);

        System.out.println("Starting background tasks");

        exportObjectPollService.init();
        playerInfoPollService.init();

        MissionStartObservable.invokeAll();

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
            new FillerRequest(Level.SERVER).send();
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
