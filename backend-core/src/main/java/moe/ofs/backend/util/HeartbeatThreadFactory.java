package moe.ofs.backend.util;

import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.RequestHandler;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

@Component
public final class HeartbeatThreadFactory implements PropertyChangeListener {

    public static boolean heartbeatActive;

    private static Thread heartbeat;

    public HeartbeatThreadFactory() {

        // listen to RequestHandler property changes
        RequestHandler.getInstance().addPropertyChangeListener(this);

        runnable = () -> {

            heartbeatActive = true;

            while(true) {

                if(isExportConnectionEstablished() && isServerConnectionEstablished()) {
                    // can connect, clear request handler trouble
                    // this also triggers background task start
                    RequestHandler.getInstance().setTrouble(false);

                    break;

                }
            }

            heartbeatActive = false;

        };
    }

    private final Runnable runnable;

    private boolean isServerConnectionEstablished() {
        return checkPortConnection(Level.SERVER_POLL);
    }

    private boolean isExportConnectionEstablished() {
        return checkPortConnection(Level.EXPORT_POLL);
    }

    private boolean checkPortConnection(Level level) {

        FillerRequest filler = new FillerRequest(level);

        return ConnectionManager.fastPackThenSendAndCheck(filler);

    }

    public synchronized Thread getHeartbeatThread() {
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

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("trouble")) {
            if((boolean) propertyChangeEvent.getNewValue()) {
                // trouble, start heartbeat
                if(!heartbeatActive) {
                    System.out.println("Starting heartbeat check...");
                    Objects.requireNonNull(getHeartbeatThread()).start();
                }
            } else {
                // no trouble, stop heartbeat
                System.out.println("Stopping heartbeat check...");
                heartbeat.interrupt();
            }
        }
    }
}
