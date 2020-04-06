package moe.ofs.backend;

import javafx.scene.Parent;

import java.io.IOException;

public interface Viewable {
    Parent getPluginGui() throws IOException;
}
