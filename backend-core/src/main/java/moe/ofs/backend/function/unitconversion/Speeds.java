package moe.ofs.backend.function.unitconversion;

import moe.ofs.backend.object.unitofmeasure.Speed;
import moe.ofs.backend.object.unitofmeasure.SystemOfMeasurement;

public class Speeds {
    public static double metersPerSecondToKnots(double mps) {
        return 1.94384 * mps;
    }

    public static double knotsToMetersPerSeconds(double kts) {
        return 0.514444 * kts;
    }

    public static double statuteMilePerHourToKnots(double mi) {
        return 0.868976 * mi;
    }

    public static double knotsToStatuteMilesPerHour(double kts) {
        return 1.15078 * kts;
    }

    public static double kilometersPerHourToKnots(double kph) {
        return 0;
    }

    public static double knotsToKilometersPerSecond(double kts) {
        return 0;
    }

    public static double kilometersPerHourToMetersPerSecond(double kph) {
        return kph / 3.6;
    }

    public static Speed of(SystemOfMeasurement system) {
        if(system.equals(SystemOfMeasurement.IMPERIAL)) {
            return Speed.KNOTS;
        } else {
            return Speed.KILOMETERS_PER_HOUR;
        }
    }
}
