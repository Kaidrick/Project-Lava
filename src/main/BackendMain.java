package main;

import core.Logger;
import core.PluginClassLoader;
import core.box.BoxOfFlyableUnit;
import core.request.BaseRequest;
import core.request.ExportPollingHandler;
import core.request.RequestHandler;
import core.request.ServerPollingHandler;
import core.request.export.handler.ExportUnitDespawnObservable;
import core.request.export.handler.ExportUnitSpawnObservable;
import core.request.server.ServerFillerRequest;
import core.request.server.handler.PlayerEnterServerObservable;
import core.request.server.handler.PlayerLeaveServerObservable;
import core.request.server.handler.PlayerSlotChangeObservable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BackendMain extends Application {

    private static final RequestHandler<BaseRequest> requestHandler = RequestHandler.getInstance();
    private static final ExportPollingHandler exportPollingHandler = ExportPollingHandler.getInstance();
    private static final ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

    static ScheduledExecutorService es;
    static ScheduledExecutorService exportPollingScheduler;
    static ScheduledExecutorService serverPollingScheduler;

    public static javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();

    private Thread background = new Thread(() -> {
        try {
            run();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    });

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static void run() throws IOException, URISyntaxException {

        BoxOfFlyableUnit.init();

        loadPlugins();

        PlayerEnterServerObservable playerEnterServerObservable =
                playerInfo -> Logger.log("New connection: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerEnterServerObservable.register();
//
        PlayerLeaveServerObservable playerLeaveServerObservable =
                playerInfo -> Logger.log("Player left: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerLeaveServerObservable.register();
//
        PlayerSlotChangeObservable playerSlotChangeObservable =
                (previous, current) -> Logger.log(
                        current.getName()
                                + " slot change: " + previous.getSlot() + " -> " + current.getSlot());
        playerSlotChangeObservable.register();
//
        ExportUnitSpawnObservable exportUnitSpawnObservable =
                unit -> Logger.log(String.format("Unit Spawn: %s (RuntimeID: %s) - %s Type",
                        unit.getUnitName(), unit.getRuntimeID(), unit.getName()));
        exportUnitSpawnObservable.register();
//
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

//        new DebugRequest("trigger.action.outText('what?', 5)", true).send();

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
        es.scheduleWithFixedDelay(mainLoop, 1000, 1, TimeUnit.MILLISECONDS);

        exportPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        exportPollingScheduler.scheduleWithFixedDelay(exportPolling,
                1000, 1, TimeUnit.MILLISECONDS);

        serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                1000, 1, TimeUnit.MILLISECONDS);


//        ScheduledExecutorService ep = Executors.newSingleThreadScheduledExecutor();
//        ep.scheduleWithFixedDelay(() -> new ServerExecRequest("return timer.getAbsTime()").send()
//                , 4000, 1000, TimeUnit.MILLISECONDS);

//        ep.scheduleWithFixedDelay(() -> new ServerLuaMemoryDataRequest().send()
//                , 2000, 1000, TimeUnit.MILLISECONDS);
//
    }

    static Path resourceToPath(URL resource)
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


    /**
     * Load plugin classes from plugin package
     * for each directory, load the class that implements Plugin interface
     */
    private static void loadPlugins() throws IOException, URISyntaxException {

        Path pluginPath = resourceToPath(ClassLoader.getSystemResource("plugin"));

        List<Path> pluginList = Files.walk(pluginPath)
                .filter(c -> c.toString().endsWith(".java") || c.toString().endsWith(".class"))
                .collect(Collectors.toList());

        Logger.log("Found " + Files.list(pluginPath).count() + " plugins with classes: "
                + pluginList.stream().map(e -> e.getFileName().toString().replace(".class", ""))
                .collect(Collectors.joining(", ")));

        PluginClassLoader pluginClassLoader = new PluginClassLoader();

        pluginList.forEach(
                p -> pluginClassLoader.invokeClassMethod(
                        "plugin" + "." +
                                p.getName(p.getNameCount() - 2) + "." +
                                p.getFileName().toString()
                                        .replace(".java", "")
                                        .replace(".class", "")
                )
        );
    }

    private static FXMLLoader loader =
            new FXMLLoader(BackendMain.class.getResource("BackendMainController.fxml"));
    private static Parent root;
    static {
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LogController logController = loader.getController();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(root, 500, 700));
        background.start();
        primaryStage.setTitle("422d Backend Control");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        background.interrupt();
        if(es != null) {
            es.shutdown();
            es.awaitTermination(10, TimeUnit.SECONDS);
        }

        if(serverPollingScheduler != null) {
            serverPollingScheduler.shutdown();
            serverPollingScheduler.awaitTermination(10, TimeUnit.SECONDS);
        }

        if(exportPollingScheduler != null) {
            exportPollingScheduler.shutdown();
            exportPollingScheduler.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
