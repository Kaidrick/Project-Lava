package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.function.unitwiselog.LogControl;
import moe.ofs.backend.function.unitwiselog.eventlogger.SpawnControlLogger;
import moe.ofs.backend.util.ConnectionManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final LogControl.Logger logger = LogControl.getLogger(RequestHandler.class);

    private Map<Level, Connection> connectionMap = new HashMap<>();

    private Map<Level, Integer> portMap = new HashMap<>();

    private Map<String, BaseRequest> waitMap = new HashMap<>();
    private volatile BlockingQueue<BaseRequest> sendQueue = new LinkedBlockingQueue<>();

    private AtomicBoolean trouble = new AtomicBoolean(true);

    private PropertyChangeSupport support;

    private static final Gson gson = new Gson();

    // TODO --> shutdown service
    private ExecutorService executorService = Executors.newCachedThreadPool();

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

    /**
     * if wait map has entry in it, query is needed
     * if wait map does not has entry in it, there is no need to send any filler request
     * @return
     */
    public boolean hasPendingServerRequest() {
//        System.out.println("server waitMap.size() = " + waitMap.size());
        return waitMap.values().stream()
//                .peek(request -> System.out.println(request.params))
                .anyMatch(r -> r.getLevel() == Level.SERVER);
    }

    public boolean hasPendingExportRequest() {
//        System.out.println("export waitMap.size() = " + waitMap.size());
        return waitMap.values().stream()
//                .peek(request -> System.out.println(request.params))
                .anyMatch(r -> r.getLevel() == Level.EXPORT);
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

            logger.warn(trouble ? "Trying to connect to DCS Lua server" : "Successfully connected to DCS Lua server");
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
    @Deprecated
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

            ConnectionManager.connectionCountIncrement();
        } catch (SocketException e) {

//            e.printStackTrace();
            // triggers background task stop
            setTrouble(true);
//            System.out.println("Trouble in RequestHandler " + LocalDateTime.now());

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
     * Request handler should maintain a pool of connection to different port
     * These connections can be put into a map
     * If one of these connections are broken / timed out, assert connection lost
     *
     * There should be a heartbeat thread to check connection to server / export
     * If a connection is established, it is put into the map
     *
     * An request of some sort must be send to the socket immediately after its creation
     */
    public void createConnections() {
        createConnections(5000);
    }

    public void createConnections(int timeout) {
        for (Map.Entry<Level, Integer> entry : portMap.entrySet()) {
            Level level = entry.getKey();
            int port = entry.getValue();

            try{
                Connection connection = new Connection("localhost", port, timeout);

                connectionMap.put(level, connection);
                log.info("Connection create: " + level + " " + connection + " at " + port);
            } catch(IOException e) {
                log.error("Unable to create connection to Lua server: at " + level + " on port " + port);

                break;  // break loop; no need to try connection on other ports
            }
        }
    }

    public Map<Level, Connection> getConnections() {
        return connectionMap;
    }

    public void shutdownConnections() {
        connectionMap.values().forEach(connection -> {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        connectionMap.clear();
    }

    public void transmissionCycle() {
        if(trouble.get()) {
            return;
        }

        Queue<BaseRequest> transmissionQueue = new ArrayDeque<>(sendQueue);

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
//                    System.out.println(level + " -> cycle -> " + json);
//                waitMap.forEach(((s, request) -> System.out.println(s + " -> " + request.getLevel() + request.params)));

                String responseJsonString;
                try {
                    responseJsonString = connectionMap.get(level).transmitAndReceive(json);
                } catch (IOException e) {
//                    e.printStackTrace();

                    setTrouble(true);
                    responseJsonString = "";
                }

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
     * Convert sendQueue to a JSON string and send it over tcp to Lua server in DCS
     */
    @Deprecated
    public void transmitAndReceive() {
//        log.info("Thread.currentThread().getName() = " + Thread.currentThread().getName());

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
                if(!json.equals("[]"))
                    System.out.println(level + " -> deprecated -> " + json);
//                waitMap.forEach(((s, request) -> System.out.println(s + " -> " + request.getLevel() + request.params)));


                String responseJsonString;
                if(portMap.get(level) != null) {
                    responseJsonString = sendAndGet(portMap.get(level), json);
                } else {
                    responseJsonString = sendAndGet(level.getPort(), json);
                }

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
