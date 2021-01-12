package moe.ofs.backend.function.motd.services;

import moe.ofs.backend.common.CrudService;
import moe.ofs.backend.function.motd.model.MotdMessageSet;

/**
 * Message of the day service is used to manage a set of message that is sent to a player who takes control of an
 * flyable aircraft.
 *
 * A message set can be associated with one or multiple roles.
 * If no role is associated with a message set, it will be sent by default
 * If no default message set is configured, no message will be sent to player on taking control of an aircraft.
 *
 * Multiple message set can be sent to player.
 */
public interface MotdManageService extends CrudService<MotdMessageSet> {
    MotdMessageSet save(MotdMessageSet message);

    void delete(MotdMessageSet message);

    void deleteById(Long id);

    void setActiveMotdSet(String name);

    void setActiveMotdSet(MotdMessageSet motdMessageSet);
}
