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
