package moe.ofs.backend.interaction;

import javafx.stage.Stage;

public class StageControl {
    private static void setShowOnParentCenter(Stage popup, Stage parentStage) {
        double centerXPosition = parentStage.getX() + parentStage.getWidth()/2d;
        double centerYPosition = parentStage.getY() + parentStage.getHeight()/2d;

        // Hide the pop-up stage before it is shown and becomes relocated
        popup.setOnShowing(windowEvent -> popup.hide());

        // Relocate the pop-up Stage
        popup.setOnShown(windowEvent -> {
            popup.setX(centerXPosition - popup.getWidth()/2d);
            popup.setY(centerYPosition - popup.getHeight()/2d);
            popup.show();
        });
    }

    public static void showOnParentCenter(Stage popup, Stage parentStage) {
        setShowOnParentCenter(popup, parentStage);

        popup.show();
    }

    public static void showOnParentCenterAndWait(Stage popup, Stage parentStage) {
        setShowOnParentCenter(popup, parentStage);

        popup.showAndWait();
    }
}
