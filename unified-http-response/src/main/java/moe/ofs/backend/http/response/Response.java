package moe.ofs.backend.http.response;

import lombok.Data;
import moe.ofs.backend.security.exception.ErrorCode;

import java.time.ZonedDateTime;

@Data
public class Response<T> {
    private int success;  // 1 - success, 0 - fail
    private int status;  // http status code
    private ZonedDateTime timestamp;  // FIXME: use long value
    private String message;
    private T data;  // data content
    private Integer errorCode;  // ErrorCode for exception handling

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

    public static Response<Object> fail() {
        Response<Object> response = new Response<>();
        response.success = 0;
        response.status = 500;
        response.timestamp = ZonedDateTime.now();

        return response;
    }
}
