package moe.ofs.backend.function.unitconversion;

import moe.ofs.backend.object.unitofmeasure.Length;
import moe.ofs.backend.object.unitofmeasure.SystemOfMeasurement;

public class Lengths {
    public static double metersToFeet(double meters) {
        return meters * 3.28084;
    }

    public static double feetToMeters(double feet) {
        return feet * 0.3048;
    }

    public static double nauticalMilesToMeters(double nauticalMiles) {
        return nauticalMiles * 1852;
    }

    public static double kilometersToMeters(double kilometers) {
        return kilometers * 1000;
    }

    public static Length of(SystemOfMeasurement system) {
        if(system.equals(SystemOfMeasurement.IMPERIAL)) {
            return Length.FEET;
        } else {
            return Length.METERS;
        }
    }
}
