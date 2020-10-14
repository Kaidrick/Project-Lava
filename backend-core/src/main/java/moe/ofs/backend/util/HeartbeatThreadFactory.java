package moe.ofs.backend.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.message.ConnectionStatusChange;
import moe.ofs.backend.message.OperationPhase;
import moe.ofs.backend.message.connection.ConnectionStatus;
import moe.ofs.backend.request.Connection;
import moe.ofs.backend.request.FillerRequest;
import moe.ofs.backend.request.RequestHandler;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public final class HeartbeatThreadFactory implements PropertyChangeListener {

    private final Sender sender;

    public static AtomicBoolean heartbeatActive;

    private static Thread heartbeat;

    private static boolean masterShutDown = false;

    public HeartbeatThreadFactory(Sender sender) {
        this.sender = sender;

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

                // if check port connection ok, flag connection to true
                if(checkPortConnection()) {
                    RequestHandler.getInstance().setTrouble(false);
                    sender.sendToTopic("connection.dcs", "String", "String");
                    Gson gson = new Gson();
                    sender.sendToTopicAsJson("connection.dcs", gson.toJson(
                            new ConnectionStatusChange(ConnectionStatus.CONNECTED)
                    ), "change");
                    break;
                }

//                if(isExportConnectionEstablished() && isServerConnectionEstablished()) {
//                    // can connect, clear request handler trouble
//                    // this also triggers background task start
//                    RequestHandler.getInstance().setTrouble(false);
//
//                    break;
//
//                }
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

    private boolean checkPortConnection() {
        // create a connection for this level and try to send a message
        RequestHandler requestHandler = RequestHandler.getInstance();
        boolean trouble = false;

        requestHandler.createConnections(1000);  // for checking only, timeout can be very low

        // if createConnections() throws exception, no connection will not be created
        // and the size of getConnections().entrySet() will be zero
        // TODO: maybe there is a better way to do it?

        if(requestHandler.getConnections().entrySet().isEmpty()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // maybe connections are empty?
            // why is connection empty thou?
            return false;  // connections are not properly established and there is trouble
        } else {
            for (Map.Entry<Level, Connection> entry : requestHandler.getConnections().entrySet()) {
                Level level = entry.getKey();
                Connection connection = entry.getValue();

                try {
                    connection.transmitAndReceive(ConnectionManager.fastPack(new FillerRequest(level)));
                } catch (IOException e) {
                    e.printStackTrace();

                    trouble = true;
                    break;  // break the loop and close all connections;
                }
            }
        }

        requestHandler.shutdownConnections();

        return !trouble;
    }

    @Deprecated
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
                heartbeat.setName("conn checker");
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
