package moe.ofs.backend.function.newslotcontrol.services;

import moe.ofs.backend.hookinterceptor.HookInterceptorDefinition;
import moe.ofs.backend.hookinterceptor.HookPredicateUtil;
import moe.ofs.backend.hookinterceptor.HookType;
import moe.ofs.backend.services.mizdb.SimpleKeyValueStorage;
import moe.ofs.backend.util.LuaScripts;
import moe.ofs.backend.util.lua.LuaQueryEnv;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SlotValidatorHookInterceptServiceTest {

    @Test
    void addDefinition() {
        List<HookInterceptorDefinition> list = new ArrayList<>();

        HookInterceptorDefinition definition = new HookInterceptorDefinition("test intc def",
                "test",
                new SimpleKeyValueStorage<String>("test kv store", LuaQueryEnv.SERVER_CONTROL),
                HookType.ON_PLAYER_TRY_CHANGE_SLOT);

        list.add(definition);
        list.add(definition);
        list.add(definition);

        Set<HookType> hookTypeSet = list.stream()
                .map(HookInterceptorDefinition::getHookType)
                .collect(Collectors.toSet());

//        String s = HookPredicateUtil.injectDefinitionHook("generic_hook_interceptor/structure/hook_target_function_struct.lua", definition);

        String k = hookTypeSet.stream().map(type ->
                HookPredicateUtil.injectDefinitionHook(
                        "generic_hook_interceptor/structure/hook_target_function_struct.lua", type))
                .collect(Collectors.joining("\n"));

        String p = LuaScripts.load("generic_hook_interceptor/create_hook.lua").replace("${hookTargetFunctionRegistry}", k);

        System.out.println(String.format(p, "test hook name", "test_name_name", true));
    }
}