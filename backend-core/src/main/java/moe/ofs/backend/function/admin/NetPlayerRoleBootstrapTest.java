package moe.ofs.backend.function.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.annotations.ListenLavaMessage;
import moe.ofs.backend.dao.PlayerRoleDao;
import moe.ofs.backend.dao.PlayerRoleGroupDao;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.domain.behaviors.net.PlayerNetActionVo;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.lang.reflect.Type;

/**
 * When a player connects to the server, check if this player is a registered player;
 * unregistered player is assigned a set of temporary role that includes some very basic authorities such
 * send chat or change slot, unless some of the roles are explicitly revoked in the record.
 *
 * An unregistered player will be put into a map as the key with a
 */

@Component
@Slf4j
public class NetPlayerRoleBootstrapTest {

    private final NetPlayerRoleService netPlayerRoleService;
    private final PlayerRoleDao playerRoleDao;
    private final PlayerRoleGroupDao playerRoleGroupDao;

    public NetPlayerRoleBootstrapTest(NetPlayerRoleService netPlayerRoleService, PlayerRoleDao playerRoleDao, PlayerRoleGroupDao playerRoleGroupDao) {
        this.netPlayerRoleService = netPlayerRoleService;
        this.playerRoleDao = playerRoleDao;
        this.playerRoleGroupDao = playerRoleGroupDao;
    }

    @ListenLavaMessage(destination = "lava.player.connection", selector = "type = 'connect'")
    public void testRoleAssignment(TextMessage textMessage) throws JMSException {

        PlayerRoleGroup roleGroup = playerRoleGroupDao.findRoleGroupWithRoles(1L);
        // TODO: find guest group and assign to guest player

        Type type = new TypeToken<PlayerNetActionVo<PlayerInfo>>() {}.getType();
        PlayerNetActionVo<PlayerInfo> actionVo = new Gson().fromJson(textMessage.getText(), type);
        PlayerInfo playerInfo = actionVo.getObject();

        if (playerInfo.getNetId() == 1) return;

        playerRoleDao.selectList(Wrappers.<PlayerRole>lambdaQuery()
                .ge(PlayerRole::getRoleLevel, 1000).le(PlayerRole::getRoleLevel, 1999))
                .forEach(role -> {
                    netPlayerRoleService.assignRole(playerInfo, role);
                    log.info("Assigning roles to player {}: {}", playerInfo.getName(), role);
                });
    }
}
