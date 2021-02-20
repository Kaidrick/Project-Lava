package moe.ofs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto extends BaseEntity {
    private String target;

    private Set<String> roles;

    private Set<String> groups;

    private Set<String> nonRoles;

    private Set<String> nonGroups;

    private boolean requiredAccessToken;

}
