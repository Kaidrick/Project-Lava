//package moe.ofs.backend.exceptions;
//
//import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
//import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.extern.slf4j.Slf4j;
//import moe.ofs.backend.dao.DcsExportObjectDao;
//import moe.ofs.backend.dao.PlayerInfoVoDao;
//import moe.ofs.backend.domain.DcsExportObject;
//import moe.ofs.backend.domain.connector.Level;
//import moe.ofs.backend.vo.PlayerInfoVo;
//import moe.ofs.backend.object.PortConfig;
//import moe.ofs.backend.connector.ConnectionManager;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("test")
//@Slf4j
//@Api("测试接口")
//public class TestForExceptionController {
//
//    private final ConnectionManager connectionManager;
//
//    private final PlayerInfoVoDao playerInfoVoDao;
//    private final DcsExportObjectDao dcsExportObjectDao;
//
//    public TestForExceptionController(ConnectionManager connectionManager,
//                                      PlayerInfoVoDao playerInfoVoDao,
//                                      DcsExportObjectDao dcsExportObjectDao) {
//        this.connectionManager = connectionManager;
//        this.playerInfoVoDao = playerInfoVoDao;
//        this.dcsExportObjectDao = dcsExportObjectDao;
//    }
//
//    @RequestMapping(value = "/test", method = RequestMethod.POST)
//    @ApiOperation("测试报错")
//    @DynamicResponseParameters(properties = {
//            @DynamicParameter(name = "UserNotFoundException", value = "test name", dataTypeClass = UserNotFoundException.class)
//    })
//    public ResponseEntity<PortConfig> testException(
//            @ApiParam(value = "entity", required = true)
//                    RequestEntity<PortConfig> entity
//    ) throws UserNotFoundException {
//        log.info(entity.toString());
//        log.info(entity.getMethod().toString());
//        log.info(entity.getType().getTypeName());
//        log.info(entity.getBody().toString());
//        throw UserNotFoundException.createWith("test name");
//
//    }
//
//    @RequestMapping(value = "/port", method = RequestMethod.GET)
//    @ApiOperation("获取当前配置")
//    @DynamicResponseParameters(properties = {
//            @DynamicParameter(name = "ResponseEntity", dataTypeClass = ResponseEntity.class)
//    })
//    public ResponseEntity<PortConfig> getCurrentConfiguration() {
//        Map<Level, Integer> portMapping = connectionManager.getPortOverrideMap();
//
//        PortConfig config = PortConfig.builder()
//                .serverMainPort(portMapping.get(Level.SERVER))
//                .serverPollPort(portMapping.get(Level.SERVER_POLL))
//                .exportMainPort(portMapping.get(Level.EXPORT))
//                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
//                .build();
//
//        return new ResponseEntity<>(config, HttpStatus.UNAUTHORIZED);
//    }
//
//    @GetMapping("/player")
//    @ApiOperation("获取玩家信息")
//    @DynamicResponseParameters(properties = {
//            @DynamicParameter(name = "PlayerInfoVo", dataTypeClass = PlayerInfoVo.class)
//    })
//    public PlayerInfoVo getPlayerTest() {
//        return playerInfoVoDao.getOneByNetIdAndName(1, "PILOT_255959");
//    }
//
//    @GetMapping("/player/{id}")
//    @ApiOperation("通过ID获取玩家信息")
//    @DynamicResponseParameters(properties = {
//            @DynamicParameter(name = "PlayerInfoVo", dataTypeClass = PlayerInfoVo.class)
//    })
//    public PlayerInfoVo getPlayerTest(
//            @ApiParam(name = "玩家ID", example = "1")
//            @PathVariable Long id
//    ) {
//        return playerInfoVoDao.selectById(id);
//    }
//
//    @GetMapping("object/{id}")
//    @ApiOperation("通过ID获取物品")
//    @DynamicResponseParameters(properties = {
//            @DynamicParameter(name = "PlayerInfoVo", dataTypeClass = PlayerInfoVo.class)
//    })
//    public DcsExportObject getObjectTest(
//            @ApiParam(name = "物体ID", example = "1")
//            @PathVariable Long id
//    ) {
//        return dcsExportObjectDao.findCompleteExportObject(id);
//    }
//}
