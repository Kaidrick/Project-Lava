package moe.ofs.backend.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.request.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Connection Manager class represents a collection of methods that can be used to deal with connection with dcs server.
 * It also holds a reference to singleton RequestHandler which handles all request sent to server
 */
public class ConnectionManager {

    private static final Gson gson = new Gson();

    private static RequestHandler requestHandler = RequestHandler.getInstance();

    /**
     * use to sanitize remaining data on lua side after backend restart
     */
    public static void sanitizeDataPipeline() {
        new FillerRequest(Level.SERVER).send();
        new FillerRequest(Level.SERVER_POLL).send();
        new FillerRequest(Level.EXPORT).send();
        new FillerRequest(Level.EXPORT_POLL).send();

        RequestHandler.getInstance().transmitAndReceive();
    }

    /**
     * pack a single base request into a container ready to sent to dcs server as a json data array.
     * @param request an instance of BaseRequest.
     * @return String value of the final json string to be sent.
     */
    public static String fastPack(BaseRequest request) {
        List<JsonRpcRequest> container = new ArrayList<>();
        container.add(request.toJsonRpcCall());
        return gson.toJson(container);
    }

    /**
     * pack a single BaseRequest into a container and check if is request has a response from server.
     * @param request an instance of BaseRequest.
     * @return boolean value indicating whether server responds to this request.
     */
    public static boolean fastPackThenSendAndCheck(BaseRequest request) {
        try {
            return requestHandler.sendAndGet(request.getPort(), fastPack(request)) != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * pack a single BaseRequest into a container and get result from dcs lua server.
     * @param request an instance of BaseRequest
     * @return A JSON string representing the result of this request
     * @throws IOException if tcp connection fails
     */
    public static String fastPackThenSendAndGet(BaseRequest request) throws IOException {
        return requestHandler.sendAndGet(request.getPort(), fastPack(request));
    }

    /**
     * This method extract a List of Response result from parsed Json String object
     * @param jsonRpcResponseList an instance of JsonPrcResponse class.
     * @param <T> Generic Type of the object the result data to be convert into.
     * @return List of said generic type.
     */
    public static <T> List<T> flattenResponse(List<JsonRpcResponse<List<T>>> jsonRpcResponseList) {
        return jsonRpcResponseList.stream()
                .flatMap(r -> r.getResult().getData().stream()).collect(Collectors.toList());
    }

    public static <T, R> List<R> flattenResponse(List<JsonRpcResponse<List<T>>> jsonRpcResponseList,
                                                                    Function<T, R> mappingFunction) {
        return jsonRpcResponseList.stream()
                .flatMap(r -> r.getResult().getData().stream())
                .map(mappingFunction)
                .collect(Collectors.toList());
    }

    public static <T> List<JsonRpcResponse<T>> parseJsonResponseToRaw(String jsonString, Class<T> targetClass) {
        // TODO --> this is so sad
        Type jsonRpcResponseListType = TypeToken.getParameterized(List.class,
                TypeToken.getParameterized(JsonRpcResponse.class, targetClass).getType()).getType();

        // TODO -> why? java.lang.IllegalStateException: Expected a string but was BEGIN_ARRAY at line 1 column 81 path $[0].result.data
        return gson.fromJson(jsonString, jsonRpcResponseListType);
    }

    public static <T> List<JsonRpcResponse<List<T>>> parseJsonResponse(String jsonString, Class<T> targetClass) {

        // TODO --> this is so sad
        Type jsonRpcResponseListType = TypeToken.getParameterized(List.class,
                TypeToken.getParameterized(JsonRpcResponse.class,
                        TypeToken.getParameterized(List.class, targetClass).getType()).getType()).getType();

        return gson.fromJson(jsonString, jsonRpcResponseListType);

//        return list.stream()
//                .flatMap(r -> r.getResult().getData().stream())
//                .map(targetClass::cast)
//                .collect(Collectors.toList());
//
//        return gson.fromJson(jsonString, jsonRpcResponseListType);
    }
}
