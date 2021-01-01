package moe.ofs.backend.function.spawncontrol.services;

import moe.ofs.backend.object.StaticObject;
import moe.ofs.backend.object.Unit;

import java.util.concurrent.CompletableFuture;

public interface StaticObjectService {
    CompletableFuture<StaticObject> addStaticObject(StaticObject staticObject);

    CompletableFuture<Integer> addStaticObject(String name, double x, double y, String type,
                                               String livery_id, String onboard_num, Double heading, int country_id);

    CompletableFuture<StaticObject> addStaticObject(Unit templateUnit, int countryId);

    CompletableFuture<Boolean> removeStaticObject(StaticObject staticObject);

    CompletableFuture<Boolean> removeStaticObject(int runtimeId);
}
