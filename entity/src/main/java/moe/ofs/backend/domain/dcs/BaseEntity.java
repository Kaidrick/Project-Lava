package moe.ofs.backend.domain.dcs;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SerializedName("generated_id")
    private Long id;  // to comply with hibernate standard

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}