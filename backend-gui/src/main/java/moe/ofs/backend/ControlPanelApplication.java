package moe.ofs.backend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.controllers.MainController;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControlPanelApplication extends Application {

    public static Stage stage;
    public static ConfigurableApplicationContext applicationContext;

    private ConfigurableApplicationContext context;

    private static Parent root;

    public static BackgroundTask task;
    public static HeartbeatThreadFactory heartbeatThreadFactory;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        this.context = new SpringApplicationBuilder() //(1)
                .sources(BackendApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));


//
//        super.init();
//        FXMLLoader loader = new FXMLLoader(ControlPanelApplication.class.getResource("/ControlPanelApplication.fxml"));
//        loader.setResources(ResourceBundle.getBundle("ControlPanelApplication", Locale.CHINA, new UTF8Control()));
//        root = loader.load();

        task = context.getBean(BackgroundTask.class);
        heartbeatThreadFactory = context.getBean(HeartbeatThreadFactory.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;

        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("ControlPanelApplication", Locale.CHINA, new UTF8Control());

        Parent root = fxWeaver.loadView(MainController.class, resourceBundle);

        Scene scene = new Scene(root);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);

        scene.getStylesheets().clear();
        scene.getStylesheets().addAll("base.css", "base_extras.css", "base_other_libraries.css", "light_theme.css");

        root.setStyle("accent_color: #854188");

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(555);
        primaryStage.setMinHeight(260);

        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(
                        ControlPanelApplication.class.getResourceAsStream("/green_bat.png")
                ))
        );

        primaryStage.setTitle(resourceBundle.getString("app_title"));
        primaryStage.show();

//         start background thread only if connect can be made
//        RequestHandler.getInstance().setTrouble(true);

        Thread heartbeat = heartbeatThreadFactory.getHeartbeatThread();
        if(heartbeat != null) {
            heartbeat.start();
        }
    }

    @Override
    public void stop() throws Exception {
        task.stop();

        ControlPanelShutdownObservable.invokeAll();

        context.close();
        Platform.exit();
    }


}
