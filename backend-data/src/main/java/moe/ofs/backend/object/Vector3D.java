package moe.ofs.backend.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class Vector3D implements Serializable {
    double x;
    double y;
    double z;

    @Override
    public String toString() {
        return getX() + ", " + getY() + ", " + getZ();
    }
}
