package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.logmanager.Logger;
import moe.ofs.backend.util.ConnectionManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/** RequestHandler class
 *  This class should be able to send and receive request
 *  request that requires a result may not be immediately available on lua side
 *  and therefore, a map should be made to track whether a request has receive a result
 *
 *  when a request that requires a result is send to lua, it should be add to the list
 *  when the request receive the result, it should let the handler know, and the handler will
 *  processes the returned data and update data locally.
 *
 */

@Slf4j
public final class RequestHandler implements PropertyChangeListener {
    //
//    private List<BaseRequest> waitList = new CopyOnWriteArrayList<>();

    private Map<Level, Integer> portMap = new HashMap<>();

    private Map<String, BaseRequest> waitMap = new HashMap<>();
    private volatile BlockingQueue<BaseRequest> sendQueue = new LinkedBlockingQueue<>();

    private AtomicBoolean trouble = new AtomicBoolean(true);

    private PropertyChangeSupport support;

    private static final Gson gson = new Gson();

    private static RequestHandler instance;

    private RequestHandler() {
        support = new PropertyChangeSupport(this);
    }

    public static synchronized RequestHandler getInstance() {
        // singleton
        // check if exists
        if(instance == null) {
            instance = new RequestHandler();

            instance.portMap = ConnectionManager.getInstance().getPortOverrideMap();
            ConnectionManager.getInstance().addPropertyChangeListener(instance);
        }
        return instance;
    }

    public void dispose() {
        waitMap.clear();
        sendQueue.clear();
    }

    // property change listener
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public boolean isTrouble() {
        return trouble.get();
    }


    /**
     * Trigger property change if and only if value changes.
     * @param trouble a boolean value indicating whether there is a trouble in connecting to DCS lua server.
     */
    public synchronized void setTrouble(boolean trouble) {
        if(this.trouble.get() != trouble) {
            log.warn(trouble ? "SocketException occurs: Failed to send request(s); set trouble flag to true"
                    : "Connection Established: set trouble flag to false");
            Logger.warn(trouble ? "Trying to connect to DCS Lua server" : "Successfully connected to DCS Lua server");
            support.firePropertyChange("trouble", this.trouble, trouble);
        }

        this.trouble.set(trouble);
    }

    // if exception is thrown here, try reconnect: check if connection can be made
    // if so, restart backend

    /**
     * If directly calling sendAndGet() method without going through standard transmission cycle,
     * the caller should check port overridden before send json to comply with potential port changes by user
     * @param port number of tcp port on which json string will be sent
     * @param jsonString the Json String to be sent to DCS Lua server
     * @return a response json string from DCS Lua server
     * @throws IOException if connection cannot be established with server
     */
    public String sendAndGet(int port, String jsonString) throws IOException {

        String s = null;
        try (Socket socket = new Socket("127.0.0.1", port);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            dataOutputStream.write((jsonString + "\n").getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();

            // java.net.ConnectionException: Connection refused: connect
            // let main thread send heartbeat signal?
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(dataInputStream, StandardCharsets.UTF_8));
            s = bufferedReader.readLine();
        } catch (SocketException e) {

            // triggers background task stop
            setTrouble(true);
            System.out.println("Trouble in RequestHandler " + LocalDateTime.now());


        }
        return s;
    }

    /**
     * takes a BaseRequest object, adds it to sendList, and send it with other BaseRequest
     * to lua server with a fixed interval?
     */
    public void take(BaseRequest request) {
        sendQueue.offer(request);
    }

    /**
     * Convert sendQueue to a JSON string and send it over tcp to Lua server in DCS
     */
    public void transmitAndReceive() {

        if(trouble.get()) {
            return;
        }

        Queue<BaseRequest> transmissionQueue = new ArrayDeque<>(sendQueue);
//        System.out.println("sendQueue = " + sendQueue);
//        System.out.println("transmissionQueue = " + transmissionQueue);
//        sendQueue.clear();  // TODO --> need more work and test

        for (int i = 0; i < transmissionQueue.size(); i++) {
            sendQueue.poll();
        }

        Map<Level, List<JsonRpcRequest>> splitQueue = transmissionQueue.stream()
                .collect(Collectors.groupingBy(BaseRequest::getLevel,
                        Collectors.mapping(BaseRequest::toJsonRpcCall, Collectors.toList())));

        // only add to wait map if result is definitely needed
        splitQueue.forEach((level, queue) -> {
            transmissionQueue.stream().filter(r -> r instanceof Resolvable).forEach(r -> waitMap.put(r.getUuid(), r));

            try {
                String json = gson.toJson(queue);
//                if(!json.equals("[]"))
//                    System.out.println(json);


                String responseJsonString;
                if(portMap.get(level) != null) {
                    responseJsonString = sendAndGet(portMap.get(level), json);
                } else {
                    responseJsonString = sendAndGet(level.getPort(), json);
                }

//                if(!s.equals("[]"))
//                    System.out.println(s);

                // received json string is a list-type
                // parse as a list of object, and each object is a subresult of a request
                // with a tag attribute with uuid of the result

                try {
                    List<JsonRpcResponse<String>> jsonRpcResponseList =
                            ConnectionManager.parseJsonResponseToRaw(responseJsonString, String.class);

                    if(jsonRpcResponseList != null) {
                        jsonRpcResponseList.forEach(
                                response -> {
                                    BaseRequest request = waitMap.remove(response.getId());

                                    // resolve Resolvable only
                                    if(request instanceof Resolvable) {
                                        ((Resolvable) request).resolve(response.getResult().getData());
                                    }
                                }
                        );
                    }
                } catch (JsonSyntaxException | IllegalStateException e) {
                    e.printStackTrace();
                    log.error(responseJsonString);
                    log.error(level + ": " + portMap.get(level) + "; " + level.getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Listen to property change of "portOverrideMap" in ConnectionManager
     * @param propertyChangeEvent
     */
    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("portOverrideMap")) {
            portMap = (Map<Level, Integer>) propertyChangeEvent.getNewValue();
        }
    }
}
