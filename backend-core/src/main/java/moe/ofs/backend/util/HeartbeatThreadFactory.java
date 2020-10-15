package moe.ofs.backend.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.message.ConnectionStatusChange;
import moe.ofs.backend.message.connection.ConnectionStatus;
import moe.ofs.backend.request.RequestHandler;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public final class HeartbeatThreadFactory {

    private final RequestHandler requestHandler;

    public static AtomicBoolean heartbeatActive;

    private static Thread heartbeat;

    private static boolean masterShutDown = false;

    private final Runnable runnable;

    public HeartbeatThreadFactory(RequestHandler requestHandler) {

        this.requestHandler = requestHandler;

        heartbeatActive = new AtomicBoolean(false);

        ControlPanelShutdownObservable observable = () -> masterShutDown = true;
        observable.register();

        // the Runnable to be run to check if connections can be established on specified ports
        runnable = () -> {
            heartbeatActive.set(true);

            do {
                if (masterShutDown) {
                    return;
                }
            } while (!checkPortConnection());

            heartbeatActive.set(false);

        };
    }

    private boolean checkPortConnection() {
        return requestHandler.checkConnections();
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

    /**
     * Listen for connection status change. If connection status changes to CONNECTED, active heartbeat checker
     * @param textMessage JSON that represents the change object
     * @throws JMSException if the text message cannot be parsed or converted correctly.
     */
    @JmsListener(destination = "dcs.connection", containerFactory = "jmsListenerContainerFactory",
            selector = "type = 'change'")
    public void connectionStatusChangeListener(TextMessage textMessage) throws JMSException {
        Gson gson = new Gson();
        ConnectionStatusChange change = gson.fromJson(textMessage.getText(), ConnectionStatusChange.class);

        if (change.getStatus() == ConnectionStatus.CONNECTED) {
            // no trouble, stop heartbeat
            heartbeat.interrupt();
            log.info("Interrupted Heartbeat Signal check thread");
        } else {
            // trouble, start heartbeat
            if(!heartbeatActive.get()) {
                Objects.requireNonNull(getHeartbeatThread()).start();
                log.info("Started Heartbeat Signal check thread");
            }
        }
    }
}
