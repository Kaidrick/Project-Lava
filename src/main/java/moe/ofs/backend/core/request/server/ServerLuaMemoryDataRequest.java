package moe.ofs.backend.core.request.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ServerLuaMemoryDataRequest extends ServerExecRequest {

    private static class MemoryUsageData {
        private long os_time;
        private float usage;

        @Override
        public String toString() {
            return "MemoryUsageData{" +
                    "os_time=" + os_time +
                    ", usage=" + usage +
                    '}';
        }
    }

    private static double minMemoryUsage = 0;
    private static double maxMemoryUsage = 0;
    private static double diffMemoryUsage = 0;

    private static final Path scriptPath = Paths.get("src/ofs.backend.core/request/scripts/get_hook_memory.lua");
    private static String luaScript;

    static {
        try {
            luaScript = Files.newBufferedReader(scriptPath).lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private transient String env;
    private transient String luaString;

    {
        handle = Handle.EXEC;
        port = 3010;
    }

    public ServerLuaMemoryDataRequest() {
        super(null);
        env = state.name().toLowerCase();
        luaString = luaScript;
    }

    @Override
    public void resolve(String data) {
        Gson gson = new Gson();
        MemoryUsageData memoryUsageData = gson.fromJson(data, MemoryUsageData.class);
        System.out.println(memoryUsageData);
    }
}
