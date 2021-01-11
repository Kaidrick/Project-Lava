package moe.ofs.backend.hookinterceptor;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Consumer;

@Data
@Builder
@EqualsAndHashCode(of = "name")
public class HookRecordProcessor<T extends HookProcessEntity> {
    private String name;
    private Consumer<T> action;
}
