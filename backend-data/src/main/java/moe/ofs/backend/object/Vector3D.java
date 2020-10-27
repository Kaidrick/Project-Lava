package moe.ofs.backend.object;

import lombok.*;
import moe.ofs.backend.domain.BaseEntity;
import moe.ofs.backend.domain.ExportObject;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Vector3D extends BaseEntity implements Serializable {

    private double x;
    private double y;
    private double z;

    @OneToOne(mappedBy = "position")
    private ExportObject exportObject;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D vector3D) {

        this.x = vector3D.getX();
        this.y = vector3D.getY();
        this.z = vector3D.getZ();
    }

    @Override
    public String toString() {
        return getX() + ", " + getY() + ", " + getZ();
    }
}
