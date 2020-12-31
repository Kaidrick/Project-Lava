package moe.ofs.backend.config.services;

/**
 * Network control service can be used to temporarily block all connections to the server.
 * Example scenario would be initialization of precached map units / objects following a server restart.
 * This ensures that the server is fully ready before any player can connect to the server.
 */
public interface DcsNetworkControlService {
    void blockServerConnections(boolean isBlocked);

    void enforceCoalitionBalance(boolean isEnforced);
    // TODO: see moe.ofs.backend.function.newslotcontrol.services.SlotManageServiceImpl.forceSlot
}
