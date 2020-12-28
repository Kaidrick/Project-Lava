package moe.ofs.backend.hookinterceptor;

import lombok.*;
import moe.ofs.backend.services.MissionPersistenceService;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class HookInterceptorDefinition {
    private String name;
    private String predicateFunction;
    private MissionPersistenceService storage;  // optional data table or kw pair name in storage
    private String decisionMappingFunction;
    private Class<?> decisionMappingClass;
    private String argPostProcessFunction;
}
