package moe.ofs.backend.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.request.server.ServerDataRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TheaterMapLandDataCollector {

    private static long row = 0;
    private static long col = 0;

    public static void fetchData() throws InterruptedException {

        ExecutorService service = Executors.newCachedThreadPool();

        AtomicLong count = new AtomicLong();

        for (int i = 0; i < 10; i++) {

            service.submit(() -> {
                for (int j = 0; j < 1000; j++) {
                    Gson gson = new Gson();
                    String luaString = LuaScripts.loadAndPrepare("util/map_theater_land_data.lua", row, col);
                    String s = ((ServerDataRequest) new ServerDataRequest(luaString).send()).get();

                    List list = gson.fromJson(s, ArrayList.class);
//                        System.out.println(list);

                    count.addAndGet(8 * 8);

                    if (row <= 6000000) {
                        row += 32;
                    } else {
                        col += 32;
                        row = 0;
                    }
                }

                log.info("done -> " + count);
            });
        }

        log.info("submit");
    }
}
