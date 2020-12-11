package moe.ofs.backend.hookinterceptor;

import moe.ofs.backend.util.LuaScripts;

public interface HookPredicateUtil {
    static String structInsert(HookInterceptorDefinition definition, String predicate) {
        String struct = LuaScripts.load("generic_hook_interceptor/structure/hook_predicate_insert_struct.lua");
        return struct.replaceAll("\\$\\{hookFunctionName}", definition.getHookType().getFunctionName())
                .replaceAll("\\$\\{hookArgsString}", definition.getHookType().getFunctionArgsString());

    }

    static String structInterceptIterator(HookInterceptorDefinition definition) {
        String struct = LuaScripts.load("generic_hook_interceptor/structure/hook_function_struct.lua");
        return struct.replaceAll("\\$\\{hookFunctionName}", definition.getHookType().getFunctionName())
                .replaceAll("\\$\\{hookArgsString}", definition.getHookType().getFunctionArgsString());

    }

    static String injectDefinitionHook(String contentScriptPath, HookInterceptorDefinition definition) {
        String struct = LuaScripts.load(contentScriptPath);

        return struct.replaceAll("\\$\\{hookFunctionName}", definition.getHookType().getFunctionName())
                .replaceAll("\\$\\{hookArgsString}", definition.getHookType().getFunctionArgsString());
    }

    static String injectDefinitionHook(String contentScriptPath, HookType type) {
        String struct = LuaScripts.load(contentScriptPath);

        return struct.replaceAll("\\$\\{hookFunctionName}", type.getFunctionName())
                .replaceAll("\\$\\{hookArgsString}", type.getFunctionArgsString());
    }
}
