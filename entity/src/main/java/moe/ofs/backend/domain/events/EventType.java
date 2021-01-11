package moe.ofs.backend.domain.events;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(EventTypeSerializer.class)
public enum EventType {
    INVALID(0),
    SHOT(1),
    HIT(2),
    TAKEOFF(3),
    LAND(4),
    CRASH(5),
    EJECTION(6),
    REFUELING(7),
    DEAD(8),
    PILOT_DEAD(9),
    BASE_CAPTURED(10),
    MISSION_START(11),
    MISSION_END(12),
    TOOK_CONTROL(13),
    REFUELING_STOP(14),
    BIRTH(15),
    HUMAN_FAILURE(16),
    DETAILED_FAILURE(17),
    ENGINE_STARTUP(18),
    ENGINE_SHUTDOWN(19),
    PLAYER_ENTER_UNIT(20),
    PLAYER_LEAVE_UNIT(21),
    PLAYER_COMMENT(22),
    SHOOTING_START(23),
    SHOOTING_END(24),
    MARK_ADDED(25),
    MARK_CHANGE(26),
    MARK_REMOVED(27),
    KILL(28),
    SCORE(29),
    UNIT_LOST(30),
    LANDING_AFTER_EJECTION(31),
    PARATROOPER_LENDING(32),
    MAX(33);

    private final int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
