package moe.ofs.backend.function.mizdb.services.impl;

import moe.ofs.backend.function.mizdb.services.LuaStorageInitService;
import moe.ofs.backend.function.mizdb.bootstrap.PersistentKeyValueInjectionBootstrap;
import moe.ofs.backend.handlers.starter.LuaScriptStarter;
import moe.ofs.backend.handlers.starter.model.ScriptInjectionTask;
import moe.ofs.backend.function.mizdb.services.MissionPersistenceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LuaStorageInitServiceImpl implements LuaStorageInitService, LuaScriptStarter {

    private final List<MissionPersistenceService> storages;

    public LuaStorageInitServiceImpl(List<MissionPersistenceService> storages) {
        this.storages = storages;
    }

    @Override
    public Map<MissionPersistenceService, Boolean> initStorages() {
        return storages.stream()
                .collect(Collectors.toMap(Function.identity(), MissionPersistenceService::createRepository));
    }

    @Override
    public ScriptInjectionTask injectScript() {
        return ScriptInjectionTask.builder()
                .scriptIdentName("LavaLuaStorageInjectRunner")
                .initializrClass(getClass())
                .dependencyInitializrClass(PersistentKeyValueInjectionBootstrap.class)
                .inject(() -> initStorages().values().stream().reduce((a, b) -> a && b).orElse(false))
                .injectionDoneCallback(aBoolean -> {
                    if (aBoolean) {
                        MissionPersistenceService.list.forEach(MissionPersistenceService::createRepository);
                    }
                })
                .build();
    }
}
