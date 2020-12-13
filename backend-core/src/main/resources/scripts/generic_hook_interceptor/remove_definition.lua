-- definition contains: name, predicate function, storage name, hook type

local hook_name = '%s'
local definition_name = '%s'

local hook = _G[hook_name]  -- reference to callback table

-- after ensuring target function hook exists, make nil
hook.predicates[definition_name] = nil
