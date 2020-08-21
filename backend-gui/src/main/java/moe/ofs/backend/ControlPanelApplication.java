package moe.ofs.backend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.controllers.MainController;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.util.HeartbeatThreadFactory;
import moe.ofs.backend.util.I18n;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControlPanelApplication extends Application {

    public static Stage stage;
    private ConfigurableApplicationContext context;

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


        task = context.getBean(BackgroundTask.class);
        heartbeatThreadFactory = context.getBean(HeartbeatThreadFactory.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;

        FxWeaver fxWeaver = context.getBean(FxWeaver.class);

        I18n.setLocale(Locale.getDefault());

        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("ControlPanelApplication", I18n.getLocale(), new UTF8Control());

        // load with system default locale
        Parent root = fxWeaver.loadView(MainController.class, resourceBundle);

        Scene scene = new Scene(root);

//        JMetro jMetro = new JMetro(Style.LIGHT);
//        jMetro.setScene(scene);

        scene.getStylesheets().clear();
        scene.getStylesheets().addAll("base.css", "base_extras.css", "base_other_libraries.css", "light_theme.css");

        root.setStyle("accent_color: purple");

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(555);
        primaryStage.setMinHeight(260);

        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(
                        ControlPanelApplication.class.getResourceAsStream("/green_bat.png")
                ))
        );

        I18n.localeProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)) {
                ResourceBundle bundle =
                        ResourceBundle.getBundle("ControlPanelApplication", newValue, new UTF8Control());
                primaryStage.setTitle(I18n.getString(bundle, "app_title"));
            }
        }));

        primaryStage.setTitle(I18n.getString(resourceBundle, "app_title"));
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
        ControlPanelShutdownObservable.invokeAll();

        BackgroundTask.getCurrentTask().stop();

        context.close();
        Platform.exit();
    }


}
