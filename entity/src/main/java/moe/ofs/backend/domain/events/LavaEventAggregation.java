package moe.ofs.backend.domain.events;

import java.util.List;

/**
 * aggregated events are a collection of events that are triggered by a common source, such as
 * an bomb explosion that may hit/destroy a few target in close proximity.
 * When this happens, the events usually have a
 */
public class LavaEventAggregation {
    List<LavaEvent> eventList;
}
