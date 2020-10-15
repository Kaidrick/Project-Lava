package moe.ofs.backend.request;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.function.unitwiselog.LogControl;
import moe.ofs.backend.jms.Sender;
import moe.ofs.backend.message.ConnectionStatusChange;
import moe.ofs.backend.message.connection.ConnectionStatus;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.stereotype.Component;

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
 * FIXME: RequestHandler should not reference any other class
 * Redesign:
 *      request handler should react to heartbeat connection status change
 *      request handler should be called to stop and clear all request and transmission
 */

@Slf4j
@Component
public final class RequestHandler {

    private final LogControl.Logger logger = LogControl.getLogger(RequestHandler.class);

    private final Sender sender;

    private Map<Level, Connection> connectionMap = new HashMap<>();

    private Map<Level, Integer> portMap = new HashMap<>();

    private Map<String, BaseRequest> waitMap = new HashMap<>();
    private volatile BlockingQueue<BaseRequest> sendQueue = new LinkedBlockingQueue<>();

    private AtomicBoolean trouble = new AtomicBoolean(true);

    // TODO --> shutdown service
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public RequestHandler(Sender sender) {
        this.sender = sender;
    }

    /**
     * if wait map has entry in it, query is needed
     * if wait map does not has entry in it, there is no need to send any filler request
     * @return boolean indicating whether there is a ServerRequest entry in the map
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


    public boolean isTrouble() {
        return trouble.get();
    }


    /**
     * This method decides whether the background task should be started.
     * It should listen for heartbeat detection, while
     * @param trouble a boolean value indicating whether there is a trouble in connecting to DCS lua server.
     */
    public synchronized void setTrouble(boolean trouble) {
        if(this.trouble.get() != trouble) {
            log.warn(trouble ? "SocketException occurs: Failed to send request(s); set trouble flag to true"
                    : "Connection Established: set trouble flag to false");

            logger.warn(trouble ? "Trying to connect to DCS Lua server" : "Successfully connected to DCS Lua server");
        }

        this.trouble.set(trouble);

        sender.sendToTopicAsJson("dcs.connection",
                new ConnectionStatusChange(trouble ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED),
                "change");
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
     * A hand shake request must be send to the socket immediately after the creation of a connection.
     */
    public void createConnections() {
        createConnections(5000);
    }

    public void createConnections(int timeout) {
        for (Map.Entry<Level, Integer> entry : portMap.entrySet()) {
            Level level = entry.getKey();
            int port = entry.getValue();

            // Attempt to create a connection and test its connectivity to dcs lua server.
            // If the connection is created and tested successfully, it is added to a map.

            // TODO: what if connections has been made, but user somehow changes the settings?
            // TODO: user may use a single web gui to connect to multiple instances of lava?

            try{
                Connection connection = new Connection("localhost", port, timeout);

                connectionMap.put(level, connection);
                log.info("Connection created: " + level + " " + connection + " at " + port);
            } catch(IOException e) {
//                e.printStackTrace();
                log.error("Unable to create connection to Lua server: at " + level + " on port " + port);

                break;  // break loop; no need to try connection on other ports
            }
        }
    }

    public Map<Level, Connection> getConnections() {
        return connectionMap;
    }

    public boolean checkConnections() {
        // create a connection for this level and try to send a message
        boolean trouble = false;

        createConnections(1000);  // for checking only, timeout can be very low

        // if createConnections() throws exception, no connection will not be created
        // and the size of getConnections().entrySet() will be zero
        // TODO: maybe there is a better way to do it?

        if(getConnections().entrySet().isEmpty()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // maybe connections are empty?
            // why is connection empty thou?
            return false;  // connections are not properly established and there is trouble
        } else {
            for (Map.Entry<Level, Connection> entry : getConnections().entrySet()) {
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

        shutdownConnections();

        // FIXME: why not set trouble directly here?
        setTrouble(trouble);

        return !trouble;
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
            transmissionQueue.stream().filter(r -> r instanceof Resolvable).forEach(r -> waitMap.put(r.getUuidString(), r));
            try {
                Gson gson = new Gson();
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
            transmissionQueue.stream().filter(r -> r instanceof Resolvable).forEach(r -> waitMap.put(r.getUuidString(), r));
            try {
                Gson gson = new Gson();
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
                // parse as a list of object, and each object is a sub-result of a request
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
     * If user change port configuration via web gui, the connection manager should call this method to update
     *  the port number mapping, so that heartbeat can be checked against correct port number.
     * @param portMap the map to be used as an override port mapping.
     */
    public void updatePortMap(Map<Level, Integer> portMap) {
        this.portMap = portMap;
    }
}
