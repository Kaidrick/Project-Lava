local success, err = pcall(function() Object.destroy({ id_ = %s }) end)

return tostring(success)
