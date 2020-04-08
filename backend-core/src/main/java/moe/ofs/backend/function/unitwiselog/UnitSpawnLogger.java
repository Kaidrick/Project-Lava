package moe.ofs.backend.function.unitwiselog;

import moe.ofs.backend.handlers.*;
import moe.ofs.backend.logmanager.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UnitSpawnLogger {

    @PostConstruct
    private void init() {
        PlayerEnterServerObservable playerEnterServerObservable =
                playerInfo -> Logger.log("New connection: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerEnterServerObservable.register();

        PlayerLeaveServerObservable playerLeaveServerObservable =
                playerInfo -> Logger.log("Player left: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerLeaveServerObservable.register();

        PlayerSlotChangeObservable playerSlotChangeObservable =
                (previous, current) -> Logger.log(
                        current.getName()
                                + " slot change: " + previous.getSlot() + " -> " + current.getSlot());
        playerSlotChangeObservable.register();

        ExportUnitSpawnObservable exportUnitSpawnObservable =
                unit -> Logger.log(String.format("Unit Spawn: %s (RuntimeID: %s) - %s Type",
                        unit.getUnitName(), unit.getRuntimeID(), unit.getName()));
        exportUnitSpawnObservable.register();

        ExportUnitDespawnObservable exportUnitDespawnObservable =
                unit -> Logger.log(String.format("Unit Despawn: %s (RuntimeID: %s) - %s Type",
                        unit.getUnitName(), unit.getRuntimeID(), unit.getName()));
        exportUnitDespawnObservable.register();
    }
}
