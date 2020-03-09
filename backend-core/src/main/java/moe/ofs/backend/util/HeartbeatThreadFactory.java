package moe.ofs.backend.util;

import moe.ofs.backend.BackgroundTask;
import moe.ofs.backend.ControlPanelApplication;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.Level;

import static moe.ofs.backend.util.ConnectionManager.fastPackThenSendAndCheck;

public final class HeartbeatThreadFactory {
    private static Thread heartbeat;

    private static boolean heartbeatStarted;

    public static synchronized boolean isHeartbeatStarted() {
        return heartbeatStarted;
    }

    public static boolean isServerConnectionEstablished() {
        return checkPortConnection(Level.SERVER_POLL);
    }

    public static boolean isExportConnectionEstablished() {
        return checkPortConnection(Level.EXPORT_POLL);
    }

    private static boolean checkPortConnection(Level level) {

        FillerRequest filler = new FillerRequest(level);

        return fastPackThenSendAndCheck(filler);

    }

    private static final Runnable runnable = () -> {
        heartbeatStarted = true;
        System.out.println("Heartbeat started");
        javafx.application.Platform.runLater(() ->
                ControlPanelApplication.logController.setConnectionStatusBarText("Waiting for connection..."));

        while(true) {
            if(isExportConnectionEstablished() && isServerConnectionEstablished()) {

                javafx.application.Platform.runLater(() ->
                        ControlPanelApplication.logController.setConnectionStatusBarText("Connected"));


                BackgroundTask.backgroundThread = new Thread(BackgroundTask.background);
                BackgroundTask.backgroundThread.start();
                break;
            }
            if(BackgroundTask.stopSign)
                break;
        }
        heartbeatStarted = false;
        System.out.println("Heartbeat stopped");
    };

    public synchronized static Thread getHeartbeatThread() {
        // if heartbeat is null, make new heartbeat
        // if heartbeat is not null, check if it is start

        if(heartbeat == null) {
            heartbeat = new Thread(runnable);
            return heartbeat;
        } else {
            if(heartbeat.getState() == Thread.State.TERMINATED) {
                heartbeat = new Thread(runnable);
                return heartbeat;
            } else {
                return null;
            }
        }
    }
}
