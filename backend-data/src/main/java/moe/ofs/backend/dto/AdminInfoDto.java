package moe.ofs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoDto extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private List<String> roles;

    private List<String> groups;

}
