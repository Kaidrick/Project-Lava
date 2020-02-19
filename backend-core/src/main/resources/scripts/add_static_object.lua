local static_object = {
    name = "%s",
    type = "%s",
    x = %f,
    y = %f,
    livery_id = "%s",
    onboard_num = "%s",
    heading = %f
}
local new_static_object = coalition.addStaticObject(%d, static_object)
return new_static_object.id_