package moe.ofs.backend.config.services.impl;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.config.services.DcsNetworkControlService;
import moe.ofs.backend.discipline.service.GlobalConnectionBlockService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DcsNetworkControlServiceImpl implements DcsNetworkControlService {

    private final GlobalConnectionBlockService blockService;

    public DcsNetworkControlServiceImpl(GlobalConnectionBlockService blockService) {
        this.blockService = blockService;
    }

    @Override
    public void blockServerConnections(boolean isBlocked) {
        if (isBlocked) {
            blockService.block("Server is in maintenance mode; please visit us later.");
        } else {
            blockService.release();
        }
    }

    @Override
    public void enforceCoalitionBalance(boolean isEnforced) {

    }
}
