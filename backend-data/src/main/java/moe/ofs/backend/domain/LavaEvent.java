package moe.ofs.backend.domain;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class LavaEvent extends SimEvent {
    private ExportObject initiator;
    private ExportObject weapon;
    private ExportObject target;

    private PlayerInfo initiatorPlayer;
    private PlayerInfo targetPlayer;

    public LavaEvent(SimEvent simEvent) {
        this.initiatorId = simEvent.initiatorId;
        this.targetId = simEvent.targetId;
        this.weaponId = simEvent.weaponId;
        this.eventId = simEvent.eventId;
        this.time = simEvent.time;
    }

    @Override
    public String toString() {
        return "LavaEvent{" +
                "eventId=" + eventId +
                ", time=" + time +
                ", initiator=" + initiator +
                ", initiatorPlayer=" + initiatorPlayer +
                ", weapon=" + weapon +
                ", target=" + target +
                ", targetPlayer=" + targetPlayer +
                '}';
    }
}
