package moe.ofs.backend.services.test;

import com.google.gson.Gson;
import moe.ofs.backend.services.MissionKeyValueService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("kvtest")
public class TestHookKeyValueController {
    private final MissionKeyValueService<String> missionKeyValueService;

    public TestHookKeyValueController(MissionKeyValueService<String> missionKeyValueService) {
        this.missionKeyValueService = missionKeyValueService;
    }

    @PostMapping("save")
    public String save(@RequestBody Map<Object, String> object) {
        System.out.println("object = " + object);
        object.entrySet().forEach(missionKeyValueService::save);
        return "OK";
    }

    @PostMapping("delete")
    public String delete(@RequestBody Map<Object, String> objectStringMap) {
        System.out.println("objectStringMap = " + objectStringMap);
        objectStringMap.keySet().forEach(missionKeyValueService::delete);
        return "OK";
    }

    @GetMapping("get_all")
    public String delete() {
        return missionKeyValueService.findAll(String.class).toString();
    }

    @PostMapping("find/{key}")
    public String find(@PathVariable String key) {
        return missionKeyValueService.find(key, String.class).orElseThrow(RuntimeException::new);
    }

    @PostMapping("fetch/{key}")
    public String fetch(@PathVariable String key) {
        return missionKeyValueService.fetch(key, String.class).orElseThrow(RuntimeException::new);
    }

    @PostMapping("fetch_all")
    public String fetchAll() {
        return missionKeyValueService.fetchAll(String.class).toString();
    }
}
