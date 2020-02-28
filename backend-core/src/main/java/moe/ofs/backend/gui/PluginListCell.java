package moe.ofs.backend.gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import moe.ofs.backend.Plugin;
import moe.ofs.backend.PluginClassLoader;

public class PluginListCell extends ListCell<String> {
    HBox hbox = new HBox();
    Label label = new Label();
    Pane pane = new Pane();
    Pane pane2 = new Pane();
    Label desc = new Label();

    Plugin plugin;

    Button controlButton = new Button();
    Button configButton = new Button("Config");

    public Button getControlButton() {
        return controlButton;
    }

    private void switchPluginLoadState() {
        // get the loaded instance of the plugin
        if(plugin.isLoaded()) {
            plugin.unregister();
            System.out.println(getItem() + " unregistered");
//            controlButton.setText("Enable");
        } else {
            plugin.register();
            System.out.println(getItem() + " registered");
//            controlButton.setText("Disable");
        }
    }

    public PluginListCell() {
        super();
        hbox.getChildren().addAll(label, pane, desc, pane2, controlButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(pane2, Priority.ALWAYS);

        controlButton.setOnAction(event -> switchPluginLoadState());
        configButton.setOnAction(event -> System.out.println("disable " + getItem()));
    }
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            plugin = PluginClassLoader.loadedPluginSet.stream()
                    .filter(p -> p.getName().equals(getItem()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Plugin Does Not Exist"));

            plugin.setPluginListCell(this);

            label.setText(item);
            controlButton.setText(plugin.isLoaded() ? "Disable" : "Enable");
            desc.setText(plugin.getDescription());
            setGraphic(hbox);
        }
    }
}
