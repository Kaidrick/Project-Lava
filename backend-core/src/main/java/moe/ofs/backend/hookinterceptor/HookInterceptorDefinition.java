package moe.ofs.backend.hookinterceptor;

import lombok.*;
import moe.ofs.backend.function.mizdb.services.MissionPersistenceService;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class HookInterceptorDefinition {
    private String name;

    @Builder.Default
    private String predicateFunction = HookInterceptorProcessService.FUNCTION_EMPTY_BLOCK;

    private MissionPersistenceService storage;  // optional data table or kw pair name in storage
    private String decisionMappingFunction;
    private Class<?> decisionMappingClass;
    private String argPostProcessFunction;
}
