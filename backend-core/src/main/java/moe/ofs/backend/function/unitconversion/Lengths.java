package moe.ofs.backend.function.unitconversion;

import moe.ofs.backend.object.unitofmeasure.Length;
import moe.ofs.backend.object.unitofmeasure.SystemOfMeasurement;

/**
 * Class Lengths is used to convert length to different unit of measurement
 * It contains static methods that takes a length value and convert it to the length under
 * the targeted system of measurement
 */
public class Lengths {
    /**
     * Convert length in metric meters to length in statue feet
     * @param meters length in metric meters
     * @return double value representing the length in statue feet
     */
    public static double metersToFeet(double meters) {
        return meters * 3.28084;
    }

    /**
     * Convert length in metric meters to length in nautical miles
     * @param meters length in metric meters
     * @return double value representing the length in nautical miles
     */
    public static double metersToNauticalMiles(double meters) {
        return meters / 1852;
    }

    /**
     * Convert length in metric meters to length in kilometers
     * @param meters length in metric meters
     * @return double value representing the length in kilometers
     */
    public static double metersToKilometer(double meters) {
        return meters / 1000;
    }

    /**
     * Convert length in metric meters to length in statue miles
     * @param meters length in metric meters
     * @return double value representing the length in statue miles
     */
    public static double metersToStatueMiles(double meters) {
        return meters * 0.000621371;
    }

    /**
     * Convert length in statue feet to length in metric meters
     * @param feet length in statue feet
     * @return double value representing the length in metric meters
     */
    public static double feetToMeters(double feet) {
        return feet * 0.3048;
    }

    /**
     * Convert length in statue feet to length in nautical miles
     * @param feet length in statue feet
     * @return double value representing the length in nautical miles
     */
    public static double feetToNauticalMiles(double feet) {
        return feet * 0.000164579;
    }

    /**
     * Convert length in statue feet to length in kilometers
     * @param feet length in statue feet
     * @return double value representing the length in kilometers
     */
    public static double feetToKilometers(double feet) {
        return feet * 0.0003048;
    }

    /**
     * Convert length in statue feet to length in statue miles
     * @param feet length in statue miles
     * @return double value representing the length in statue miles
     */
    public static double feetToStatueMiles(double feet) {
        return feet / 5280;
    }

    /**
     * Convert length in nautical miles to length in metric meters
     * @param nauticalMiles length in nautical miles
     * @return double value representing the length in metric meters
     */
    public static double nauticalMilesToMeters(double nauticalMiles) {
        return nauticalMiles * 1852;
    }

    /**
     * Convert length in nautical miles to length in statue miles
     * @param nauticalMiles length in nautical miles
     * @return double value representing the length in statue miles
     */
    public static double nauticalMilesToStatueMiles(double nauticalMiles) {
        return nauticalMiles * 1.15078;
    }

    /**
     * Convert length in nautical miles to length in statue feet
     * @param nauticalMiles length in nautical miles
     * @return double value representing the length in statue feet
     */
    public static double nauticalMilesToFeet(double nauticalMiles) {
        return nauticalMiles * 6076.12;
    }

    /**
     * Convert length in nautical miles to length in metric kilometers
     * @param nauticalMiles length in nautical miles
     * @return double value representing the length in metric kilometers
     */
    public static double nauticalMilesToKilometers(double nauticalMiles) {
        return nauticalMiles * 1.852;
    }

    /**
     * Convert length in metric kilometers to length in metric meters
     * @param kilometers length in metric kilometers
     * @return double value representing the length in metric meters
     */
    public static double kilometersToMeters(double kilometers) {
        return kilometers * 1000;
    }

    /**
     * Convert length in metric kilometers to length in nautical miles
     * @param kilometer length in metric kilometers
     * @return double value representing the length in nautical miles
     */
    public static double kilometersToNauticalMiles(double kilometer) {
        return kilometer / 1.852;
    }

    /**
     * Convert length in metric kilometers to length in statue feet
     * @param kilometers length in metric kilometers
     * @return double value representing the length in statue feet
     */
    public static double kilometersToFeet(double kilometers) {
        return kilometers * 3280.84;
    }

    /**
     * Convert length in metric kilometers to length in statue miles
     * @param kilometers length in metric kilometers
     * @return double value representing the length in statue miles
     */
    public static double kilometersToStatueMiles(double kilometers) {
        return kilometers * 0.621371;
    }

    /**
     * Convert length in statue miles to length in meters
     * @param statueMiles length in statue miles
     * @return double value representing the length in metric meters
     */
    public static double statusMilesToMeters(double statueMiles) {
        return statueMiles * 1609.34;
    }

    /**
     * Convert length in statue miles to length in nautical miles
     * @param statueMiles length in statue miles
     * @return double value representing the length in nautical miles
     */
    public static double statusMilesToNauticalMiles(double statueMiles) {
        return statueMiles * 0.868976;
    }

    /**
     * Convert length in statue miles to length in metric kilometers
     * @param statueMiles length in statue miles
     * @return double value representing the length in metric kilometers
     */
    public static double statueMilesToKilometers(double statueMiles) {
        return statueMiles * 1.60934;
    }

    /**
     * Convert length in statue miles to length in statue feet
     * @param statueMiles length in statue miles
     * @return double value representing the length in statue feet
     */
    public static double statueMilesToFeet(double statueMiles) {
        return statueMiles * 5280;
    }

    /**
     * Returns the standard definition length unit used in given system of measurement
     * @param system enum
     * @return Length enum representing the unit of measurement
     */
    public static Length of(SystemOfMeasurement system) {
        if(system.equals(SystemOfMeasurement.IMPERIAL)) {
            return Length.FEET;
        } else {
            return Length.METERS;
        }
    }
}
