package moe.ofs.backend.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.UTF8Control;
import moe.ofs.backend.Viewable;
import moe.ofs.backend.interaction.StageControl;
import moe.ofs.backend.util.I18n;

import java.io.IOException;
import java.util.ResourceBundle;

public class PluginListCell extends ListCell<Plugin> {
    Scene scene;
    Stage pluginStage;

    HBox iconByContent = new HBox(10);
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
    Button configButton = new Button();

    private void switchPluginLoadState(Plugin plugin) {
        // get the loaded instance of the plugin
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("ControlPanelApplication", I18n.getLocale(), new UTF8Control());
        if(plugin.isEnabled()) {
            plugin.disable();
            controlButton.setText(resourceBundle.getString("plugin_control_enable"));
        } else {
            plugin.enable();
            controlButton.setText(resourceBundle.getString("plugin_control_disable"));
        }
    }

    public PluginListCell(ListView<Plugin> listView) {
        super();

        title.getChildren().addAll(label, version, pane, author);
        HBox.setHgrow(pane, Priority.ALWAYS);
        mainVBox.getChildren().addAll(title, desc, control);
        control.getChildren().addAll(controlButton);

        iconByContent.getChildren().add(mainVBox);
    }

    @Override
    protected void updateItem(Plugin item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            plugin = Plugin.loadedPlugins.stream()
                    .filter(p -> p.equals(getItem()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Plugin Does Not Exist"));

            ResourceBundle resourceBundle =
                    ResourceBundle.getBundle("ControlPanelApplication", I18n.getLocale(), new UTF8Control());

            if(plugin instanceof Viewable) {
                configButton = new Button(resourceBundle.getString("plugin_control_config"));
                control.getChildren().add(configButton);

                I18n.localeProperty().addListener(((observable, oldValue, newValue) -> {
                    ResourceBundle newBundle =
                            ResourceBundle.getBundle("ControlPanelApplication", I18n.getLocale(),
                                    new UTF8Control());

                    configButton.setText(newBundle.getString("plugin_control_config"));
                }));
            }

            ImageView imageView = plugin.getIcon() != null ? new ImageView(plugin.getIcon()) :
                    new ImageView(new Image(getClass().getResourceAsStream("/plugin_default_icon.png")));
            imageView.setFitHeight(32);
            imageView.setFitWidth(32);
            iconByContent.getChildren().add(0, imageView);

            if(plugin.getLocalizedName() != null) {
                label.setText(plugin.getLocalizedName());
            } else {
                label.setText(plugin.getName());
            }
            I18n.localeProperty().addListener(observable -> {
                if(plugin.getLocalizedName() != null) {
                    label.setText(plugin.getLocalizedName());
                }
            });

            label.setStyle("-fx-font-weight: bold;");

            version.setText(plugin.getVersion());
            author.setText(plugin.getAuthor());


            controlButton.setText(plugin.isEnabled() ?
                    resourceBundle.getString("plugin_control_disable") :
                    resourceBundle.getString("plugin_control_enable"));

            I18n.localeProperty().addListener(((observable, oldValue, newValue) -> {
                ResourceBundle newBundle =
                        ResourceBundle.getBundle("ControlPanelApplication", newValue,
                                new UTF8Control());
                controlButton.setText(plugin.isEnabled() ?
                        newBundle.getString("plugin_control_disable") :
                        newBundle.getString("plugin_control_enable"));
            }));



            if(plugin.getLocalizedDescription() != null) {
                desc.setText(plugin.getLocalizedDescription());
            } else {
                desc.setText(plugin.getDescription());
            }
            I18n.localeProperty().addListener(observable -> {
                if(plugin.getLocalizedDescription() != null) {
                    desc.setText(plugin.getLocalizedDescription());
                }
            });

            controlButton.setOnAction(event -> switchPluginLoadState(plugin));
            configButton.setOnAction(event -> {
                // must be a Viewable
                Viewable viewable = (Viewable) plugin;
                try {
                    if(scene == null) {
                        Parent parent = viewable.getPluginGui();
                        if(parent != null) {
                            scene = new Scene(parent);
                        }
                    }

                    JMetro jMetro = new JMetro(Style.LIGHT);
                    jMetro.setScene(scene);

                    if(pluginStage == null) {
                        pluginStage = new Stage();

                        if(plugin.getIcon() != null) {
                            pluginStage.getIcons().add(plugin.getIcon());
                        } else {
                            pluginStage.getIcons().add(
                                    new Image(getClass().getResourceAsStream("/plugin_default_icon.png"))
                            );
                        }

                        pluginStage.setScene(scene);
                    }

                    if(plugin.getLocalizedName() != null) {
                        pluginStage.setTitle(plugin.getLocalizedName());

                        I18n.localeProperty().addListener(observable -> {
                            pluginStage.setTitle(plugin.getLocalizedName());
                        });
                    } else {
                        pluginStage.setTitle(plugin.getName());
                    }


                    if(!pluginStage.isShowing()) {
                        StageControl.showOnParentCenter(pluginStage, ControlPanelApplication.stage);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            setGraphic(iconByContent);
        }
    }
}
