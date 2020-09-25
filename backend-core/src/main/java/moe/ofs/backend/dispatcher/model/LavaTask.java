package moe.ofs.backend.dispatcher.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.domain.BaseEntity;

/**
 * LavaTask class represents a task that can be dispatched by a LavaTaskDispatcher.
 * The dispatcher maintains a list of a map of task, and these tasks can be tracked or
 */

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class LavaTask extends BaseEntity {
    private Boolean isPeriodic;
    private long interval;
    private Runnable task;

    private String name;
    private Class<?> source;
}
