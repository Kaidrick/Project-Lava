package moe.ofs.backend.util;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("airdata")
@Api(tags = "用户角色管理管理API")
public class AirdromeDataController {

    @GetMapping("collect")
    @ApiOperation(value = "收集机场信息")
    public String collectData() {
        new Thread(AirdromeDataCollector::collect).start();
        return "OK";
    }
}
