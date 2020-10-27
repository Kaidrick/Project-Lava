package moe.ofs.backend.exceptions;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.dao.DcsExportObjectDao;
import moe.ofs.backend.dao.PlayerInfoVoDao;
import moe.ofs.backend.domain.DcsExportObject;
import moe.ofs.backend.domain.Level;
import moe.ofs.backend.domain.PlayerInfoVo;
import moe.ofs.backend.object.PortConfig;
import moe.ofs.backend.util.ConnectionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("test")
@Slf4j
public class ExceptionTestController {

    private final ConnectionManager connectionManager;

    private final PlayerInfoVoDao playerInfoVoDao;
    private final DcsExportObjectDao dcsExportObjectDao;

    public ExceptionTestController(ConnectionManager connectionManager,
                                   PlayerInfoVoDao playerInfoVoDao,
                                   DcsExportObjectDao dcsExportObjectDao) {
        this.connectionManager = connectionManager;
        this.playerInfoVoDao = playerInfoVoDao;
        this.dcsExportObjectDao = dcsExportObjectDao;
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity<PortConfig> testException(RequestEntity<PortConfig> entity) throws UserNotFoundException {
        log.info(entity.toString());
        log.info(entity.getMethod().toString());
        log.info(entity.getType().getTypeName());
        log.info(entity.getBody().toString());
        throw UserNotFoundException.createWith("test name");

    }

    @RequestMapping(value = "/port", method = RequestMethod.GET)
    public ResponseEntity<PortConfig> getCurrentConfiguration() {
        Map<Level, Integer> portMapping = connectionManager.getPortOverrideMap();

        PortConfig config = PortConfig.builder()
                .serverMainPort(portMapping.get(Level.SERVER))
                .serverPollPort(portMapping.get(Level.SERVER_POLL))
                .exportMainPort(portMapping.get(Level.EXPORT))
                .exportPollPort(portMapping.get(Level.EXPORT_POLL))
                .build();

        return new ResponseEntity<>(config, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/player")
    public PlayerInfoVo getPlayerTest() {
        return playerInfoVoDao.getOneByNetIdAndName(1, "PILOT_255959");
    }

    @GetMapping("/player/{id}")
    public PlayerInfoVo getPlayerTest(@PathVariable Long id) {
        return playerInfoVoDao.selectById(id);
    }

    @GetMapping("object/{id}")
    public DcsExportObject getObjectTest(@PathVariable Long id) {
        return dcsExportObjectDao.findWithPosById(id);
    }
}
