local group_data =
{
    ["modulation"] = 0,
    ["tasks"] =
    {
    }, -- end of ["tasks"]
    ["radioSet"] = false,
    ["task"] = "CAS",
    ["uncontrolled"] = true,
    ["route"] =
    {
        ["points"] =
        {
            [1] =
            {
                -- ["alt"] = 567,
                ["action"] = "From Parking Area",
                ["alt_type"] = "BARO",
                -- ["speed"] = 41.666666666667,
                ["type"] = "TakeOffParking",
                -- ["ETA"] = 0,
                -- ["ETA_locked"] = true,
                -- ["y"] = -17126.658203125,
                -- ["x"] = -396434.90625,
                ["name"] = "Test Point",
                ["airdromeId"] = 4,
                ["speed_locked"] = true,
            }, -- end of [1]
        }, -- end of ["points"]
    }, -- end of ["route"]
    ["groupId"] = 252,
    ["hidden"] = false,
    ["units"] =
    {
        [1] =
        {
            -- ["livery_id"] = "USA X Black",
            -- ["skill"] = "High",
            ["parking"] = "%d",
            -- ["ropeLength"] = 15,
            -- ["speed"] = 41.666666666667,
            ["type"] = "OH-58D",
            -- ["unitId"] = 252,
            -- ["parking_id"] = "H04",
            -- ["heading"] = 0,
            ["name"] = "Test Unit",
        }, -- end of [1]
    }, -- end of ["units"]
    ["name"] = "Test Group",
} -- end of [1]

coalition.addGroup(country.id.USA, Group.Category.HELICOPTER, group_data)