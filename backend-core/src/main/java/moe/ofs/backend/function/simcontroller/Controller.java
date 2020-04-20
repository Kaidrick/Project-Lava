package moe.ofs.backend.function.simcontroller;

import moe.ofs.backend.object.Unit;
import moe.ofs.backend.object.command.Command;
import moe.ofs.backend.object.tasks.Task;

import java.util.List;

public interface Controller {
    void setTask(Task task);

    void resetTask();

    void pushTask(Task task);

    void popTask();

    boolean hasTask(Task task);

    void setCommand(Command command);

    void setOption();

    void setOnOff();

    void knowTarget();

    boolean isTargetDetected(Unit target);

    List<Unit> getDetectedTargets();
}
