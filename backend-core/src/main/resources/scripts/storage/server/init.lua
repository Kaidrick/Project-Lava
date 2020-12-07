-- create global db table
__storage = __storage or {}

-- mission state storage can only be clean manually and explicitly

-- Meta class
DataTable = {
  name = nil,
  nextId = 0,
  repository = nil
}

-- Base class method new
function DataTable:new(table_name)
   o = o or {}
   setmetatable(o, self)
   self.__index = self
   self.name = table_name
   self.repository = {}

   return o
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