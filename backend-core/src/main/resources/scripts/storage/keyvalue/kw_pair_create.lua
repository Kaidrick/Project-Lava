__storage = __storage or {}

local pair_name = '%s'

-- check for existing table
if not __storage[pair_name] then
    local kwPair = KwPair:new(pair_name)
    __storage[pair_name] = kwPair
end

return "true"
