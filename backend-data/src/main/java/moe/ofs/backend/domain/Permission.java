package moe.ofs.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {
    private String[] target;

    private Set<String> roles = new HashSet<>();

    private Set<String> groups = new HashSet<>();

    private Set<String> nonRoles = new HashSet<>();

    private Set<String> nonGroups = new HashSet<>();

    private boolean requiredAccessToken = true;
}
