package moe.ofs.backend.connector;

import moe.ofs.backend.domain.connector.OperationPhase;
import moe.ofs.backend.domain.connector.handlers.scripts.ScriptInjectionTask;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LavaSystemStatus {
    private static OperationPhase phase;
    private static String theater;
    private static Instant startTime;
    private static Map<ScriptInjectionTask, Boolean> injectionTaskChecks;
    private static boolean initiated;

    public static OperationPhase getPhase() {
        return phase;
    }

    public static void setPhase(OperationPhase phase) {
        LavaSystemStatus.phase = phase;
    }

    public static String getTheater() {
        return theater;
    }

    public static void setTheater(String theater) {
        LavaSystemStatus.theater = theater;
    }

    public static Instant getStartTime() {
        return startTime;
    }

    public static void setStartTime(Instant startTime) {
        LavaSystemStatus.startTime = startTime;
    }

    public static void setInjectionTaskChecks(Map<ScriptInjectionTask, Boolean> injectionTaskChecks) {
        LavaSystemStatus.injectionTaskChecks = injectionTaskChecks;
    }

    public static Map<ScriptInjectionTask, Boolean> getInjectionTaskChecks() {
        return new HashMap<>(injectionTaskChecks);
    }

    public static boolean isInitiated() {
        return initiated;
    }

    public static void setInitiated(boolean initiated) {
        LavaSystemStatus.initiated = initiated;
    }
}
