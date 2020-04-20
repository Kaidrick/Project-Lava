package moe.ofs.backend.object.tasks;

import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.object.command.Command;

import java.util.List;

@Getter
@Setter
public class WrappedActionParams {
    private Command action;
}
