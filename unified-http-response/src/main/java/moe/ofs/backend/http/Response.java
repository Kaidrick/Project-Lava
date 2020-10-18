package moe.ofs.backend.http;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Response<T> {
    private int success;  // 1 - success, 0 - fail
    private int status;  // status code
    private ZonedDateTime timestamp;  // time
    private String message;
    private T data;  // data content

    public static Response<Object> success() {
        Response<Object> response = new Response<>();
        response.success = 1;
        response.status = 200;
        response.timestamp = ZonedDateTime.now();

        return response;
    }

    public static Response<Object> success(Object data) {
        Response<Object> response = success();
        response.data = data;

        return response;
    }

    public static Response<Object> fail(Object data) {
        Response<Object> response = new Response<>();
        response.success = 1;
        response.status = 500;
        response.timestamp = ZonedDateTime.now();
        response.data = data;

        return response;
    }
}
