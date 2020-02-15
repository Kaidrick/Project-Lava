package ofs.backend.core.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

public final class RequestHandler<T extends BaseRequest> {
    //
//    private List<BaseRequest> waitList = new CopyOnWriteArrayList<>();
    private Map<String, BaseRequest> waitMap = new HashMap<>();
    private Map<String, String> resultMap = new HashMap<>();  // request ident, result
    private volatile Queue<BaseRequest> sendQueue = new ArrayDeque<>();

    private static final Gson gson = new Gson();

    private static RequestHandler<BaseRequest> instance;

    private RequestHandler() { }

    public static synchronized RequestHandler<BaseRequest> getInstance() {
        // singleton
        //check if exists
        if(instance == null) {
            instance = new RequestHandler<>();
        }
        return instance;
    }

    public static String sendAndGet(int port, String jsonString) throws IOException {

        Socket socket = new Socket("127.0.0.1", port);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream.write((jsonString + "\n").getBytes(StandardCharsets.UTF_8));
        dataOutputStream.flush();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(dataInputStream, StandardCharsets.UTF_8));
        String s = bufferedReader.readLine();
        dataOutputStream.close();
        socket.close();
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

        Queue<BaseRequest> transmissionQueue = new ArrayDeque<>(sendQueue);
//        System.out.println("sendQueue = " + sendQueue);
//        System.out.println("transmissionQueue = " + transmissionQueue);
//        sendQueue.clear();  // TODO --> need more work and test

        for (int i = 0; i < transmissionQueue.size(); i++) {
            sendQueue.poll();
        }

        Map<Integer, List<JsonRpcRequest>> splitQueue = transmissionQueue.stream()
                .collect(Collectors.groupingBy(BaseRequest::getPort,
                        Collectors.mapping(BaseRequest::toJsonRpcCall, Collectors.toList())));

//        splitQueue.get(3010).stream().filter(e -> e.toString().contains("EXEC")).forEach(System.out::println);

//        if(!transmissionQueue.toString().equals("[]"))
//        System.out.println(transmissionQueue);

//        if(!splitQueue.toString().equals("{}"))
//        System.out.println(splitQueue);

        splitQueue.forEach((port, queue) -> {
            transmissionQueue.forEach(r -> waitMap.put(r.getUuid(), r));

            try {
                String json = gson.toJson(queue);
//                if(!json.equals("[]"))
//                    System.out.println(json);

                String s = sendAndGet(port, json);

//                if(!s.equals("[]"))
//                    System.out.println(s);

                // received json string is a list-type
                // parse as a list of object, and each object is a subresult of a request
                // with a tag attribute with uuid of the result

                Type jsonRpcResponseListType = new TypeToken<List<JsonRpcResponse<String>>>() {}.getType();
                List<JsonRpcResponse<String>> jsonRpcResponseList = gson.fromJson(s, jsonRpcResponseListType);

                jsonRpcResponseList.forEach(
                        r -> waitMap.computeIfPresent(r.getId(),
                            (k, v) -> {
//                                System.out.println(v.getClass().toString());
                                v.resolve(r.getResult().getData());
                                return null;
                            }));

            } catch (Exception e) {
                e.printStackTrace();
            }

            // deserialize should be done separately? as per class?

        });
    }

}