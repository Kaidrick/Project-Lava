package moe.ofs.backend.object;


/**
 * Airbase class represent the combination of info from mission.env and scripting engine
 * Airbase data should be collected and serialized into file and should be read in runtime
 * In order to be able to manually input the initial heading info,
 * Airbase class should have a field of "parking name" such as F135
 *
 * ["parking_id"] = "F116" in unit
 * ["parking"] = 59 in unit
 * ["type"] = "TakeOffParking" in route.points[1]
 * ["airdromeId"] = 4 in route.points[1]
 *
 * check if this parking is the same number as in the parking info queried from SSE
 * put a aircraft in every parking
 * let the mission run and match env data with query data by parking?
 * --> parking_id --> parking index? Term_Index?
 *
 * For each airdromeId, dynamically spawn a unit in each parking
 *
 */
public class Airbase {

}
