package moe.ofs.backend.simevent.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.*;
import moe.ofs.backend.dataservice.exportobject.ExportObjectService;
import moe.ofs.backend.dataservice.graveyard.GraveyardService;
import moe.ofs.backend.dataservice.player.PlayerInfoService;
import moe.ofs.backend.domain.dcs.poll.ExportObject;
import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.domain.events.LavaEvent;
import moe.ofs.backend.domain.events.SimEvent;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class SimEventRegistryMapService extends AbstractMapService<SimEvent> implements SimEventRegistryService {

    private final ExportObjectService exportObjectService;
    private final GraveyardService graveyardService;
    private final PlayerInfoService playerInfoService;

    public SimEventRegistryMapService(ExportObjectService exportObjectService, GraveyardService graveyardService,
                                      PlayerInfoService playerInfoService) {
        this.exportObjectService = exportObjectService;
        this.graveyardService = graveyardService;
        this.playerInfoService = playerInfoService;
    }


    /**
     * Check association in export object and graveyard; if any of the initiator, weapon, or target is not found,
     * postpone to a later point of time and search again in said services.
     * @param event SimEvent object to associate with ExportObject
     * @return SimEvent object that has associated ExportObject set/partially set, or the original SimEvent is
     *         no association can be found at all.
     */
    @Override
    public SimEvent associate(SimEvent event) {
        LavaEvent lavaEvent;
        if (event instanceof LavaEvent) {
            lavaEvent = (LavaEvent) event;
        } else {
            lavaEvent = new LavaEvent(event);
        }

        lavaEvent.incrementRetryCount();

        referenceInitiator(lavaEvent, event.getInitiatorId(), lavaEvent.getInitiator());
        referenceTarget(lavaEvent, event.getTargetId(), lavaEvent.getTarget());
        referenceWeapon(lavaEvent, event.getWeaponId(), lavaEvent.getWeapon());

        if ((lavaEvent.getInitiatorId() != 0 && lavaEvent.getInitiator() == null) ||
            (lavaEvent.getTargetId() != 0 && lavaEvent.getTarget() == null) ||
            (lavaEvent.getWeaponId() != 0 && lavaEvent.getWeapon() == null)) {
            // event has initiator but no association can be made, postpone to next cycle
            lavaEvent.setAssociated(false);
        } else {
            if (lavaEvent.getInitiator() != null && lavaEvent.getInitiator().getStatus().get("Human")) {
                // associate player, assuming player slot remains for sufficient amount of time
                playerInfoService.findByName(lavaEvent.getInitiator().getUnitName())
                        .ifPresent(lavaEvent::setInitiatorPlayer);
            }

            if (lavaEvent.getTarget() != null && lavaEvent.getTarget().getStatus().get("Human")) {
                // associate player, assuming player slot remains for sufficient amount of time
                playerInfoService.findByName(lavaEvent.getTarget().getUnitName())
                        .ifPresent(lavaEvent::setTargetPlayer);
            }

            lavaEvent.setAssociated(true);
        }
        return lavaEvent;  // incomplete association
    }

    private void referenceTarget(LavaEvent event, long runtimeId, ExportObject check) {
        if (check != null) {  // already associated
            return;
        }

        // check for initiator
        Optional<ExportObject> optional = exportObjectService.findByRuntimeId(runtimeId);
        if (optional.isPresent()) {
            event.setTarget(optional.get());
        } else {  // unable to associate in service, try graveyard

            Optional<GraveyardRecord> optionalGraveyardRecord =
                    graveyardService.findByRecordRuntimeId(runtimeId);

            if (optionalGraveyardRecord.isPresent()) {
                event.setTarget(optionalGraveyardRecord.get().getRecord());
                log.info("delegate to graveyard search for event id {}, runtime id {}",
                        event.getType(), event.getTargetId());
            } else {
                log.info("unable to find target for event id {}, runtime id {}",
                        event.getType(), event.getTargetId());
            }
        }
    }

    private void referenceWeapon(LavaEvent event, long runtimeId, ExportObject check) {
        if (check != null) {  // already associated
            return;
        }

        // check for weapon
        Optional<ExportObject> optional = exportObjectService.findByRuntimeId(runtimeId);
        if (optional.isPresent()) {
            event.setWeapon(optional.get());
        } else {  // unable to associate in service, try graveyard

            Optional<GraveyardRecord> optionalGraveyardRecord =
                    graveyardService.findByRecordRuntimeId(runtimeId);

            if (optionalGraveyardRecord.isPresent()) {
                event.setWeapon(optionalGraveyardRecord.get().getRecord());
                log.info("delegate to graveyard search for event id {}, runtime id {}",
                        event.getType(), event.getWeaponId());
            } else {
                log.info("unable to find weapon for event id {}, runtime id {}",
                        event.getType(), event.getWeaponId());
            }
        }
    }

    private void referenceInitiator(LavaEvent event, long runtimeId, ExportObject check) {
        if (check != null) {  // already associated
            return;
        }

        // check for initiator
        Optional<ExportObject> optional = exportObjectService.findByRuntimeId(runtimeId);
        if (optional.isPresent()) {
            event.setInitiator(optional.get());
        } else {  // unable to associate in service, try graveyard

            Optional<GraveyardRecord> optionalGraveyardRecord =
                    graveyardService.findByRecordRuntimeId(runtimeId);

            if (optionalGraveyardRecord.isPresent()) {
                event.setInitiator(optionalGraveyardRecord.get().getRecord());
                log.info("delegate to graveyard search for event id {}, runtime id {}",
                        event.getType(), event.getInitiatorId());
            } else {
                log.info("unable to find initiator for event id {}, runtime id {}",
                        event.getType(), event.getInitiatorId());
            }
        }
    }
}
