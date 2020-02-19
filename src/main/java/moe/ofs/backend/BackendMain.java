package moe.ofs.backend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.controllers.MainController;
import moe.ofs.backend.core.box.BoxOfFlyableUnit;
import moe.ofs.backend.core.request.BaseRequest;
import moe.ofs.backend.core.request.ExportPollingHandler;
import moe.ofs.backend.core.request.RequestHandler;
import moe.ofs.backend.core.request.ServerPollingHandler;
import moe.ofs.backend.core.request.export.handler.ExportUnitDespawnObservable;
import moe.ofs.backend.core.request.export.handler.ExportUnitSpawnObservable;
import moe.ofs.backend.core.request.server.ServerFillerRequest;
import moe.ofs.backend.core.request.server.handler.PlayerEnterServerObservable;
import moe.ofs.backend.core.request.server.handler.PlayerLeaveServerObservable;
import moe.ofs.backend.core.request.server.handler.PlayerSlotChangeObservable;
import moe.ofs.backend.util.UTF8Control;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


// auto connect and reconnect
// if no exception is thrown, run background task
// if exception is thrown, halt all schedulers and run heartbeat signal thread

// if
public class BackendMain extends Application {

    private static final RequestHandler<BaseRequest> requestHandler = RequestHandler.getInstance();
    private static final ExportPollingHandler exportPollingHandler = ExportPollingHandler.getInstance();
    private static final ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

    static ScheduledExecutorService es;
    static ScheduledExecutorService exportPollingScheduler;
    static ScheduledExecutorService serverPollingScheduler;

    public static boolean needRestart;
    public static boolean stopSign;

    public static AtomicBoolean isHalted = new AtomicBoolean(false);

    private static FXMLLoader loader =
            new FXMLLoader(BackendMain.class.getResource("/BackendMainController.fxml"));
    private static Parent root;
    static {
        try {
            loader.setResources(ResourceBundle.getBundle("BackendMain", Locale.CHINA, new UTF8Control()));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MainController logController = loader.getController();


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

        shutdownExecutorService(es);
        shutdownExecutorService(serverPollingScheduler);
        shutdownExecutorService(exportPollingScheduler);

        isHalted.set(true);
    }


    public static Runnable background = () -> {
        try {
            startBackgroundTask();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    };
    public static Thread backgroundThread = new Thread(background);


    public static Thread heartbeatSignalThread;



    public static void main(String[] args) {
        Application.launch(args);
    }


    // restart background task when connect is cut
    public static void startBackgroundTask() throws IOException, URISyntaxException {

        isHalted.set(false);

        System.out.println("Starting background tasks");

        ConnectionManager.sanitizeDataPipeline(requestHandler);

        exportPollingHandler.init();
        serverPollingHandler.init();

//        BoxOfParking.init();

        BoxOfFlyableUnit.init();

//        Plugin.loadPlugins();

        MissionStartObservable.invokeAll();

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



        Runnable exportPolling = exportPollingHandler::poll;

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
        es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleWithFixedDelay(mainLoop, 0, 1, TimeUnit.MILLISECONDS);

        exportPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        exportPollingScheduler.scheduleWithFixedDelay(exportPolling,
                0, 1, TimeUnit.MILLISECONDS);

        serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                0, 1, TimeUnit.MILLISECONDS);
    }

    public static Path resourceToPath(URL resource)
            throws IOException,
            URISyntaxException {

        Objects.requireNonNull(resource, "Resource URL cannot be null");
        URI uri = resource.toURI();

        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            return Paths.get(uri);
        }

        if (!scheme.equals("jar")) {
            throw new IllegalArgumentException("Cannot convert to Path: " + uri);
        }

        String s = uri.toString();
        int separator = s.indexOf("!/");
        String entryName = s.substring(separator + 2);
        URI fileURI = URI.create(s.substring(0, separator));

        FileSystem fs = FileSystems.newFileSystem(fileURI,
                Collections.emptyMap());
        return fs.getPath(entryName);
    }


    interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    public static void processResource(URI uri, IOConsumer<Path> action) throws IOException {
        try {
            Path p=Paths.get(uri);
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


    @Override
    public void start(Stage primaryStage) throws Exception {
        JMetro jMetro = new JMetro(Style.LIGHT);
        Scene scene = new Scene(root);

        jMetro.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(555);
        primaryStage.setMinHeight(260);
        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(
                        ClassLoader.getSystemResourceAsStream("green_bat.png")
                ))
        );

        primaryStage.setTitle(loader.getResources().getString("app_title"));
        primaryStage.show();

        // start background thread only if connect can be made
        Thread heartbeat = HeartbeatThreadFactory.getHeartbeatThread();
        if(heartbeat != null) {
            heartbeat.start();
        }
    }

    @Override
    public void stop() throws Exception {
        halt();
        if(heartbeatSignalThread != null)
            heartbeatSignalThread.interrupt();

        // global stop flag
        stopSign = true;

        moe.ofs.backend.Application.ctx.close();
    }
}
