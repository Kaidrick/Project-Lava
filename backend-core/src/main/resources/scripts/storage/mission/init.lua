-- create global db table
__storage = __storage or {}

-- add handler to drop database on mission restart
local handler = {}
handler.ident = "__storageDropOnMissionRestart"
handler.f = function(event)
    if event.id == world.event.S_EVENT_MISSION_START then
        __storage = {}
    end
end

function handler:onEvent(event)
    self.f(event)
end
world.addEventHandler(handler)

-- Meta class
DataTable = {}
DataTable.__index = DataTable

-- Base class method new
function DataTable:new(table_name)
  return setmetatable({
    name = table_name,
    nextId = 0,
    repository = {}
  }, DataTable)
end

function DataTable:allocate(table_name)
    local table = DataTable:new(table_name)
    __storage[table_name] = table
    return __storage[table_name]
end

function DataTable:save(object)
  self.nextId = self.nextId + 1
  self.repository[self.nextId] = object

  return self.nextId
end

function DataTable:findBy(attribute_name, value)
  for _, data in pairs(self.repository) do
    if data[attribute_name] == value then
      return data
    end
  end
end

function DataTable:deleteBy(attribute_name, value)
  for index, data in pairs(self.repository) do
    if data[attribute_name] == value then
      self.repository[index] = nil
    end
  end
end

function DataTable:delete(object)
  for index, data in pairs(self.repository) do
    if data.generated_id and object.generated_id then
      if data.generated_id == object.generated_id then
        self.repository[index] = nil
      end
    end
  end
end

function DataTable:deleteAll()
  self.repository = {}
  self.nextId = 0
end

function DataTable:deleteById(index)
  self.repository[index] = nil
end

function DataTable:findById(index)
  return self.repository[index]
end

function DataTable:findAll()
  local data = {}
  for _, value in pairs(self.repository) do
    table.insert(data, value)
  end
  return data
end

setmetatable(DataTable, { __call = DataTable.new })

return "true"