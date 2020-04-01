package moe.ofs.backend.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.PluginClassLoader;

import java.io.IOException;

public class PluginListCell extends ListCell<Plugin> {
    VBox mainVBox = new VBox(5);
    HBox title = new HBox(10);
    HBox control = new HBox(5);
    Pane pane = new Pane();

    Label label = new Label();
    Label author = new Label();
    Label version = new Label();
    Label desc = new Label();

    Plugin plugin;

    Button controlButton = new Button();
    Button configButton = new Button("Config");

    private void switchPluginLoadState(Plugin plugin) {
        // get the loaded instance of the plugin
        if(plugin.isEnabled()) {
            plugin.disable();
            controlButton.setText("Enable");
        } else {
            plugin.enable();
            controlButton.setText("Disable");
        }
    }

    public PluginListCell(ListView<Plugin> listView) {
        super();

        title.getChildren().addAll(label, version, pane, author);
        HBox.setHgrow(pane, Priority.ALWAYS);
        mainVBox.getChildren().addAll(title, desc, control);
        control.getChildren().addAll(controlButton);

        itemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                System.out.println("listener -> " + newValue);
                plugin = Plugin.loadedPlugins.stream()
                        .filter(p -> p.equals(getItem()))
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("Plugin Does Not Exist"));

                try {
                    if(plugin.getPluginGui() != null)
                        control.getChildren().add(configButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                label.setText(plugin.getName());
                label.setStyle("-fx-font-weight: bold;");

                version.setText("version placeholder");
                author.setText("author placeholder");
                controlButton.setText(plugin.isEnabled() ? "Disable" : "Enable");
                desc.setText(plugin.getDescription());

                System.out.println("pass plugin var -> " + plugin);

                controlButton.setOnAction(event -> switchPluginLoadState(plugin));
                configButton.setOnAction(event -> {
                    try {
                        if(plugin.getPluginGui() != null) {
                            Parent root = plugin.getPluginGui();
                            Scene scene = new Scene(root);
                            JMetro jMetro = new JMetro(Style.LIGHT);
                            jMetro.setScene(scene);

                            Stage pluginStage = new Stage();
                            pluginStage.setScene(scene);
                            pluginStage.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                setGraphic(mainVBox);
            }
        }));

    }

//    @Override
//    protected void updateItem(Plugin item, boolean empty) {
//        super.updateItem(item, empty);
//        setText(null);
//        setGraphic(null);
//
//        if (item != null && !empty) {
//            plugin = PluginClassLoader.loadedPluginSet.stream()
//                    .filter(p -> p.equals(getItem()))
//                    .findAny()
//                    .orElseThrow(() -> new RuntimeException("Plugin Does Not Exist"));
//
//            try {
//                if(plugin.getPluginGui() != null)
//                    control.getChildren().add(configButton);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            label.setText(item.getName());
//            label.setStyle("-fx-font-weight: bold;");
//
//            version.setText("version placeholder");
//            author.setText("author placeholder");
//            controlButton.setText(plugin.isEnabled() ? "Disable" : "Enable");
//            desc.setText(plugin.getDescription());
//            setGraphic(mainVBox);
//        }
//    }
}
