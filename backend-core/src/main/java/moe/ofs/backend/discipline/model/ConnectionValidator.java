package moe.ofs.backend.discipline.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConnectionValidator {
    private String name;
    private String function;
    private String description;
}
