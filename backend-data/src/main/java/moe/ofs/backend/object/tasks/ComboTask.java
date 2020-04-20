package moe.ofs.backend.object.tasks;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComboTask extends Task {

    private ComboTaskParams params;

    {
        id = "ComboTask";
    }
}


/*
["id"] = "ComboTask",
    ["params"] =
    {
        ["tasks"] =
        {
            [1] =
            {
                ["enabled"] = true,
                ["auto"] = true,
                ["id"] = "WrappedAction",
                ["number"] = 1,
                ["params"] =
                {
                    ["action"] =
                    {
                        ["id"] = "EPLRS",
                        ["params"] =
                        {
                            ["value"] = true,
                            ["groupId"] = 1,
                        }, -- end of ["params"]
                    }, -- end of ["action"]
                }, -- end of ["params"]
            }, -- end of [1]
            [2] =
            {
                ["enabled"] = true,
                ["auto"] = false,
                ["id"] = "Aerobatics",
                ["number"] = 2,
                ["params"] =
                {
                    ["maneuversSequency"] =
                    {
                        [1] =
                        {
                            ["displayName"] = "直线飞行",
                            ["name"] = "STRAIGHT_FLIGHT",
                            ["params"] =
                            {
                                ["InitSpeed"] =
                                {
                                    ["order"] = 3,
                                    ["value"] = 807.57239661831,
                                }, -- end of ["InitSpeed"]
                                ["InitAltitude"] =
                                {
                                    ["order"] = 2,
                                    ["value"] = 6705.6,
                                }, -- end of ["InitAltitude"]
                                ["StartImmediatly"] =
                                {
                                    ["order"] = 5,
                                    ["value"] = 1,
                                }, -- end of ["StartImmediatly"]
                                ["UseSmoke"] =
                                {
                                    ["order"] = 4,
                                    ["value"] = 0,
                                }, -- end of ["UseSmoke"]
                                ["FlightTime"] =
                                {
                                    ["min_v"] = 1,
                                    ["max_v"] = 200,
                                    ["value"] = 200,
                                    ["step"] = 0.1,
                                    ["order"] = 6,
                                }, -- end of ["FlightTime"]
                                ["RepeatQty"] =
                                {
                                    ["min_v"] = 1,
                                    ["max_v"] = 10,
                                    ["value"] = 10,
                                    ["order"] = 1,
                                }, -- end of ["RepeatQty"]
                            }, -- end of ["params"]
                        }, -- end of [1]
                    }, -- end of ["maneuversSequency"]
                    ["maneuversParams"] =
                    {
                    }, -- end of ["maneuversParams"]
                }, -- end of ["params"]
            }, -- end of [2]
        }, -- end of ["tasks"]
    }, -- end of ["params"]
}, -- end of ["task"]

* */
