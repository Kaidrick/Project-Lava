package moe.ofs.backend.security.service;

import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.dto.AdminInfoDto;

public interface PermissionService {
    Long add(AdminInfoDto adminInfo);

    AdminInfoDto getOneById(Long id);

    AdminInfoDto getOneByName(String name);

    void collect();

    AdminInfoDto adminInfoToDto(AdminInfo adminInfo);

    void delete(Long id);

    void deleteByName(String name);
}
