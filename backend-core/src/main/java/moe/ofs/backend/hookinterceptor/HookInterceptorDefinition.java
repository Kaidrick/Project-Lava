package moe.ofs.backend.hookinterceptor;

import lombok.*;
import moe.ofs.backend.services.MissionPersistenceService;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class HookInterceptorDefinition {
    private String name;
    private String predicateFunction;
    private MissionPersistenceService storage;  // optional data table or kw pair name in storage
    private HookType hookType;

    HookInterceptorDefinition(String name, String predicateFunction, HookType hookType) {
        this.predicateFunction = predicateFunction;
        this.name = name;
        this.hookType = hookType;
    }
}
