package moe.ofs.backend.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import moe.ofs.backend.domain.dcs.BaseEntity;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class BaseUserInfoDto extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public BaseUserInfoDto(String className) {
        this.className = className;
    }

    public String name;

    public List<String> roles;

    public List<String> groups;

    public String className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
