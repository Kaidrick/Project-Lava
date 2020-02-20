package moe.ofs.backend.object;

import java.io.Serializable;

public class Vector3D implements Serializable {
    double x;
    double y;
    double z;

    @Override
    public String toString() {
        return getX() + ", " + getY() + ", " + getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
