package moe.ofs.backend.function.motd.services.impl;

import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.function.motd.model.MotdMessageSet;
import moe.ofs.backend.function.motd.services.MotdManageService;
import moe.ofs.backend.function.triggermessage.model.Message;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Service
public class MotdManageServiceImpl extends AbstractMapService<MotdMessageSet> implements MotdManageService {
    private MotdMessageSet activeMotdMessageSet;

    public MotdManageServiceImpl() {
        // default to the first set or read from configuration file
//        findAll().stream()
//                .min(Comparator.comparingLong(MotdMessageSet::getLastEditTime))
//                .ifPresent(m -> activeMotdSetName = m.getName());
        MotdMessageSet motdMessageSet = new MotdMessageSet();
        Set<Message> messages = new HashSet<>();
        Message test1 = new Message("test 1 for 5s", 5);
        Message test2 = new Message("test 2 for 10s", 10);
        Message test3 = new Message("test 3 for 2s", 2);
        messages.add(test1);
        messages.add(test2);
        messages.add(test3);

        motdMessageSet.setMessages(messages);
        motdMessageSet.setCreateTime(System.currentTimeMillis());
        motdMessageSet.setName("test motd msg set");
        motdMessageSet.setRoles(null);
        save(motdMessageSet);
    }

    @Override
    public void setActiveMotdSet(String name) {
//        activeMotdSetName = name;
    }

    @Override
    public void setActiveMotdSet(MotdMessageSet motdMessageSet) {
//        activeMotdSetName = motdMessageSet.getName();
    }
}
