package moe.ofs.backend.http.response;

import moe.ofs.backend.http.PageResponse;
import moe.ofs.backend.http.Response;

import javax.management.ReflectionException;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;

public class Responses {
    public static Response<Object> success() {
        Response<Object> response = new Response<>();
        response.setSuccess(1);
        response.setStatus(200);
        response.setTimestamp(ZonedDateTime.now());

        return response;
    }

    public static Response<Object> success(Object data) {
        Response<Object> response = success();
        response.setData(data);

        return response;
    }

    public static Response<Object> fail() {
        Response<Object> response = new Response<>();
        response.setSuccess(1);
        response.setStatus(500);
        response.setTimestamp(ZonedDateTime.now());

        return response;
    }

    public static Response<Object> querySuccess(Object data)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {

        Class<?> clazz = data.getClass();

        if (!clazz.getName().equals("moe.ofs.backend.pagination.PageVo")) {
            throw new ClassNotFoundException();
        }

        Field currentField = clazz.getDeclaredField("current");
        Field totalField = clazz.getDeclaredField("total");
        Field dataField = clazz.getDeclaredField("data");

        currentField.setAccessible(true);
        totalField.setAccessible(true);
        dataField.setAccessible(true);

        PageResponse<Object> response = new PageResponse<>();
        response.setSuccess(1);
        response.setStatus(200);
        response.setTimestamp(ZonedDateTime.now());
        response.setCurrent((Long) currentField.get(data));
        response.setTotal((Long) totalField.get(data));
        response.setData(dataField.get(data));

        return response;
    }
}
