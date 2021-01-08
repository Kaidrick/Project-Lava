package moe.ofs.backend.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import moe.ofs.backend.LavaLog;
import moe.ofs.backend.domain.message.ConnectionStatusChange;
import moe.ofs.backend.domain.message.connection.ConnectionStatus;
import moe.ofs.backend.connector.ConnectionManager;
import moe.ofs.backend.connector.RequestHandler;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class HeartbeatThreadFactory {

    private final RequestHandler requestHandler;

    private final ConnectionManager connectionManager;

    public static AtomicBoolean heartbeatActive;

    private static Thread heartbeat;

    private static boolean masterShutDown = false;

    private final Runnable runnable;

    private static LavaLog.Logger logger = LavaLog.getLogger(HeartbeatThreadFactory.class);

    public HeartbeatThreadFactory(RequestHandler requestHandler, ConnectionManager connectionManager) {

        this.requestHandler = requestHandler;
        this.connectionManager = connectionManager;

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

        if (heartbeat == null || heartbeat.getState() == Thread.State.TERMINATED) {
            heartbeat = new Thread(runnable);
            heartbeat.setName("conn checker");

            String portString = connectionManager.getPortOverrideMap().values().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            logger.info(String.format("Heartbeat checker scanning connections on following ports: %s",
                    portString));

            return heartbeat;
        }
        return null;
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
