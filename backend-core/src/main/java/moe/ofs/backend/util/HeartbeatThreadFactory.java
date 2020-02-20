package moe.ofs.backend.util;

import com.google.gson.Gson;
import moe.ofs.backend.BackendMain;
import moe.ofs.backend.request.JsonRpcRequest;
import moe.ofs.backend.request.RequestHandler;
import moe.ofs.backend.request.server.ServerFillerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class HeartbeatThreadFactory {
    private static Thread heartbeat;

    private static boolean heartbeatStarted;

    public static synchronized boolean isHeartbeatStarted() {
        return heartbeatStarted;
    }

    public static boolean isServerConnectionEstablished() {
        return checkPortConnection(3011);
    }

    public static boolean isExportConnectionEstablished() {
        return checkPortConnection(3013);
    }

    private static boolean checkPortConnection(int port) {
        Gson gson = new Gson();

        List<JsonRpcRequest> container = new ArrayList<>();
        ServerFillerRequest filler = new ServerFillerRequest();

        container.add(filler.toJsonRpcCall());
        String json = gson.toJson(container);

        try {
            return RequestHandler.sendAndGet(port, json) != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final Runnable runnable = () -> {
        heartbeatStarted = true;
        System.out.println("Heartbeat started");
        javafx.application.Platform.runLater(() ->
                BackendMain.logController.setConnectionStatusBarText("Waiting for connection..."));

        while(true) {
            if(isExportConnectionEstablished() && isServerConnectionEstablished()) {

                javafx.application.Platform.runLater(() ->
                        BackendMain.logController.setConnectionStatusBarText("Connected"));


                BackendMain.backgroundThread = new Thread(BackendMain.background);
                BackendMain.backgroundThread.start();
                break;
            }
            if(BackendMain.stopSign)
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
