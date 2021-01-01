package moe.ofs.backend.function.spawncontrol.services.impl;

import moe.ofs.backend.function.spawncontrol.services.StaticObjectService;
import moe.ofs.backend.object.StaticObject;
import moe.ofs.backend.object.Unit;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Async
public class StaticObjectServiceImpl implements StaticObjectService {
    @Override
    public CompletableFuture<StaticObject> addStaticObject(StaticObject staticObject) {
        int runtimeId = LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING, "add_static_object.lua",
                staticObject.getName(), staticObject.getType(),
                staticObject.getX(), staticObject.getY(), staticObject.getLivery_id(),
                staticObject.getOnboard_num(), staticObject.getHeading(), staticObject.getCountry_id()).getAsInt();

        staticObject.setId(runtimeId);
        return CompletableFuture.completedFuture(staticObject);
    }

    @Override
    public CompletableFuture<Integer> addStaticObject(String name, double x, double y, String type,
                                                      String livery_id, String onboard_num, Double heading, int country_id) {
        return CompletableFuture.completedFuture(LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING, "add_static_object.lua",
                name, type,
                x, y, livery_id,
                onboard_num, heading, country_id).getAsInt());

    }

    @Override
    public CompletableFuture<StaticObject> addStaticObject(Unit templateUnit, int countryId) {
        StaticObject staticObject = StaticObject.builder()
                .name(templateUnit.getName())
                .x(templateUnit.getX())
                .y(templateUnit.getY())
                .heading(templateUnit.getHeading())
                .livery_id(templateUnit.getLivery_id())
                .type(templateUnit.getType())
                .onboard_num(templateUnit.getOnboard_num())
                .country_id(countryId)
                .build();

        return addStaticObject(staticObject);
    }

    @Override
    public CompletableFuture<Boolean> removeStaticObject(StaticObject staticObject) {
        return removeStaticObject(staticObject.getId());
    }

    @Override
    public CompletableFuture<Boolean> removeStaticObject(int runtimeId) {
        return CompletableFuture.completedFuture(LuaScripts.requestWithFile(LuaQueryEnv.MISSION_SCRIPTING,
                "spawn_control/remove_object_by_runtime_id.lua", runtimeId).getAsBoolean());
    }
}
