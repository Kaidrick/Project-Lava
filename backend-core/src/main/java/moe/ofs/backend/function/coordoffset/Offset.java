package moe.ofs.backend.function.coordoffset;

import moe.ofs.backend.object.Vector3D;

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
}
