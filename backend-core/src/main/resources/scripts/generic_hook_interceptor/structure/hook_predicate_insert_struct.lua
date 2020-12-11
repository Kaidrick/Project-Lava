local hook_name = '%s'

_G[hook_name].predicate['${hookFunctionName}'] = _G[hook_name].predicate['${hookFunctionName}'] or {}

table.insert(_G[hook_name].predicate['${hookFunctionName}'], %s)