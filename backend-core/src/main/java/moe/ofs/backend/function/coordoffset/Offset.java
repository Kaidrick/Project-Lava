package moe.ofs.backend.function.coordoffset;

import moe.ofs.backend.function.unitconversion.Lengths;
import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.object.unitofmeasure.Length;

public class Offset {
    public static Vector3D of(double dist, double bearing, Vector3D from) {
        double normAngle = bearing;

        System.out.println("norm angle is " + normAngle / Math.PI + " PI");

        // rotate as in z-x plane
        double x = from.getX();
        double z= from .getZ();

        double xf = x + dist * Math.cos(normAngle);
        double zf = z + dist * Math.sin(normAngle);

        return new Vector3D(xf, from.getY(), zf);
    }

    public static double convert(double meters, Length baseUnit) {
        switch (baseUnit) {
            case FEET:
                return Lengths.metersToFeet(meters);
            default:
                return meters;
        }
    }

    public static double slantRange(Vector3D from, Vector3D to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) +
                Math.pow(from.getY() - to.getY(), 2) +
                Math.pow(from.getZ() - to.getZ(), 2));
    }

    public static double distance(Vector3D from, Vector3D to) {
        return Math.hypot(Math.abs(to.getX() - from.getX()),
                Math.abs(to.getZ() - from.getZ()));
    }

    public static double distance(Vector3D from, Vector3D to, Length baseUnit) {
        double dist = distance(from, to);
        double convertedValue;
        switch (baseUnit) {
            case METERS:
                break;
            case FEET:
                break;
            case KILOMETERS:
                break;
            case STATUTE_MILES:
                break;
            case NAUTICAL_MILES:
                break;
            default:
                break;
        }

        return convertedValue = 0.0;
    }
}
