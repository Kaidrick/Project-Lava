package moe.ofs.backend.function.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.ofs.backend.annotations.ListenLavaMessage;
import moe.ofs.backend.dao.PlayerRoleDao;
import moe.ofs.backend.domain.admin.PlayerRole;
import moe.ofs.backend.domain.behaviors.net.PlayerNetActionVo;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.lang.reflect.Type;

@Component
public class NetPlayerRoleBootstrapTest {

    private final NetPlayerRoleService netPlayerRoleService;
    private final PlayerRoleDao playerRoleDao;

    public NetPlayerRoleBootstrapTest(NetPlayerRoleService netPlayerRoleService, PlayerRoleDao playerRoleDao) {
        this.netPlayerRoleService = netPlayerRoleService;
        this.playerRoleDao = playerRoleDao;
    }

    @ListenLavaMessage(destination = "lava.player.connection", selector = "type = 'connect'")
    public void testRoleAssignment(TextMessage textMessage) throws JMSException {
        Type type = new TypeToken<PlayerNetActionVo<PlayerInfo>>() {}.getType();
        PlayerNetActionVo<PlayerInfo> actionVo = new Gson().fromJson(textMessage.getText(), type);
        PlayerInfo playerInfo = actionVo.getObject();

        playerRoleDao.selectList(Wrappers.<PlayerRole>lambdaQuery().le(PlayerRole::getRoleLevel, 2))
                .forEach(role -> netPlayerRoleService.assignRole(playerInfo, role));
    }
}
