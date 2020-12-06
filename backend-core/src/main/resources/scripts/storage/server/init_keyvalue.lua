-- create global db table
__storage = __storage or {}

-- mission state storage can only be clean manually and explicitly

-- Meta class
KwPair = {
  name = nil,
  repository = nil
}

-- Base class method new
function KwPair:new(table_name)
   o = o or {}
   setmetatable(o, self)
   self.__index = self
   self.name = table_name
   self.repository = {}

   return o
end

function KwPair:save(key, value)
  self.repository[key] = value
  return key
end

function KwPair:get(key)
  return self.repository[key]
end

function KwPair:find(value)  -- find key by value
  for k, v in pairs(self.repository) do
    if v == value then
      return k
    end
  end
end

function KwPair:delete(key) -- delete by key
  net.log("kwpair delete " .. key)
  self.repository[key] = nil
end

function KwPair:deleteAll()
  self.repository = {}
end

function KwPair:findAll()
  local data = {}
  for _, value in pairs(self.repository) do
    table.insert(data, value)
  end
  return data
end