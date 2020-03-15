package moe.ofs.backend.util;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.RequestHandler;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public final class HeartbeatThreadFactory implements PropertyChangeListener {

    public static AtomicBoolean heartbeatActive;

    private static Thread heartbeat;

    private static boolean masterShutDown = false;

    public HeartbeatThreadFactory() {

        heartbeatActive = new AtomicBoolean(false);

        ControlPanelShutdownObservable observable = () -> masterShutDown = true;
        observable.register();

        // listen to RequestHandler property changes
        RequestHandler.getInstance().addPropertyChangeListener(this);

        runnable = () -> {

            heartbeatActive.set(true);

            while(true) {

                if(masterShutDown) {
                    return;
                }

                if(isExportConnectionEstablished() && isServerConnectionEstablished()) {
                    // can connect, clear request handler trouble
                    // this also triggers background task start
                    RequestHandler.getInstance().setTrouble(false);

                    break;

                }
            }

            heartbeatActive.set(false);

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
            heartbeat.setName("conn checker");
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
                if(!heartbeatActive.get()) {
                    Objects.requireNonNull(getHeartbeatThread()).start();
                    log.info("Started Heartbeat Signal check thread");
                }
            } else {
                // no trouble, stop heartbeat
                heartbeat.interrupt();
                log.info("Interrupted Heartbeat Signal check thread");
            }
        }
    }
}
