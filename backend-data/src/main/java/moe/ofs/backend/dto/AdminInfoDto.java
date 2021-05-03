package moe.ofs.backend.dto;

import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AdminInfoDto extends BaseUserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    public AdminInfoDto() {
        super("AdminInfoDto");
    }

    public AdminInfoDto(String name, List<String> roles, List<String> groups, String className) {
        super(name, roles, groups, "AdminInfoDto");
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public List<String> getRoles() {
        return super.getRoles();
    }

    @Override
    public void setRoles(List<String> roles) {
        super.setRoles(roles);
    }

    @Override
    public List<String> getGroups() {
        return super.getGroups();
    }

    @Override
    public void setGroups(List<String> groups) {
        super.setGroups(groups);
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public String getClassName() {
        return super.getClassName();
    }

    @Override
    public void setClassName(String className) {
        super.setClassName(className);
    }
}
