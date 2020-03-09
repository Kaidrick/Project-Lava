package moe.ofs.backend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.handlers.*;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import org.springframework.context.ConfigurableApplicationContext;


import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControlPanelApplication extends Application {

    public static Stage stage;
    public static ConfigurableApplicationContext applicationContext;

    private static Parent root;
    public static MainController logController;

    public static BackgroundTask task;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        FXMLLoader loader = new FXMLLoader(ControlPanelApplication.class.getResource("/ControlPanelApplication.fxml"));
        loader.setResources(ResourceBundle.getBundle("ControlPanelApplication", Locale.CHINA, new UTF8Control()));
        root = loader.load();
        logController = loader.getController();

        task = applicationContext.getBean(BackgroundTask.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Scene scene = new Scene(root);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(555);
        primaryStage.setMinHeight(260);

        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(
                        ControlPanelApplication.class.getResourceAsStream("/green_bat.png")
                ))
        );

        primaryStage.setTitle("422d Backend Control Panel");
        primaryStage.show();

//         start background thread only if connect can be made
        Thread heartbeat = HeartbeatThreadFactory.getHeartbeatThread();
        if(heartbeat != null) {
            heartbeat.start();
        }
    }

    @Override
    public void stop() throws Exception {
        task.halt();
        if(task.heartbeatSignalThread != null)
            task.heartbeatSignalThread.interrupt();

        // global stop flag
        task.stopSign = true;

        ControlPanelShutdownObservable.invokeAll();
    }


}
