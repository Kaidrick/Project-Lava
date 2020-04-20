package moe.ofs.backend.function.simcontroller;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import moe.ofs.backend.object.Unit;
import moe.ofs.backend.object.command.Command;
import moe.ofs.backend.object.tasks.Task;
import moe.ofs.backend.request.RequestToServer;
import moe.ofs.backend.request.server.ServerActionRequest;
import moe.ofs.backend.util.LuaScripts;

import java.util.List;

@Getter
@Setter
public class UnitController implements Controller {
    private long runtimeId;

    private Unit unit;

    public UnitController(Unit unit) {
        this.unit = unit;
    }

    @Override
    public void setTask(Task task) {
        Gson gson = new Gson();
        ServerActionRequest request = new ServerActionRequest(RequestToServer.State.MISSION,
                LuaScripts.loadAndPrepare("simcontroller/controller_set_task_for_unit.lua",
                        unit.getId(), gson.toJson(task)));
        System.out.println("request = " + request);
        request.send();
    }

    @Override
    public void resetTask() {

    }

    @Override
    public void pushTask(Task task) {

    }

    @Override
    public void popTask() {

    }

    @Override
    public boolean hasTask(Task task) {
        return false;
    }

    @Override
    public void setCommand(Command command) {

    }

    @Override
    public void setOption() {

    }

    @Override
    public void setOnOff() {

    }

    @Override
    public void knowTarget() {

    }

    @Override
    public boolean isTargetDetected(Unit target) {
        return false;
    }

    @Override
    public List<Unit> getDetectedTargets() {
        return null;
    }
}
