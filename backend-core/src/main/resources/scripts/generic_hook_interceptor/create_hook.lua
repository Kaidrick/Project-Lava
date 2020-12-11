local hook_name = '%s'

_G[hook_name] = {

    predicate = {},  -- creation is always destructive

}  -- create hook lua table

DCS.setUserCallbacks(_G[hook_name])