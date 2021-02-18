package moe.ofs.backend.function.motd;

import moe.ofs.backend.domain.admin.PlayerRoleGroup;
import moe.ofs.backend.function.admin.services.NetPlayerRoleService;
import moe.ofs.backend.domain.admin.message.MotdMessageSet;
import moe.ofs.backend.function.motd.services.MotdManageService;
import moe.ofs.backend.domain.admin.message.Message;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MotdBootstrapTest {
    private final MotdManageService motdManageService;
    private final NetPlayerRoleService netPlayerRoleService;

    public MotdBootstrapTest(MotdManageService motdManageService, NetPlayerRoleService netPlayerRoleService) {
        this.motdManageService = motdManageService;
        this.netPlayerRoleService = netPlayerRoleService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        PlayerRoleGroup group = netPlayerRoleService.findRoleGroupByName("guest");

        MotdMessageSet motdMessageSet = new MotdMessageSet();
        Set<Message> messages = motdMessageSet.getMessages();
        Message test1 = new Message("test 1 for 5s", 5);
        test1.setIndex(1);
        Message test2 = new Message("test 2 for 10s", 10);
        test2.setIndex(2);
        Message test3 = new Message("test 3 for 2s", 2);
        test3.setIndex(3);
        messages.add(test1);
        messages.add(test2);
        messages.add(test3);

        motdMessageSet.setCreateTime(System.currentTimeMillis());
        motdMessageSet.setLastEditTime(motdMessageSet.getCreateTime());
        motdMessageSet.setName("test motd msg set");
        motdMessageSet.getAssignedRoleGroups().add(group);

        motdManageService.save(motdMessageSet);
    }
}
