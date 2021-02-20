package moe.ofs.backend.security.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import moe.ofs.backend.common.AbstractMapService;
import moe.ofs.backend.dao.AdminInfoDao;
import moe.ofs.backend.domain.AdminInfo;
import moe.ofs.backend.dto.AdminInfoDto;
import moe.ofs.backend.security.service.AdminInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminInfoMapService extends AbstractMapService<AdminInfoDto> implements AdminInfoService {
    private final AdminInfoDao adminInfoDao;

    public Long add(AdminInfoDto adminInfo) {
        return save(adminInfo).getId();
    }

    public AdminInfoDto getOneById(Long id) {
        Optional<AdminInfoDto> dto = findById(id);
        if (dto.isPresent()) return dto.get();

        AdminInfo adminInfo = adminInfoDao.selectById(id);
        if (adminInfo == null) throw new RuntimeException("您输入的用户ID有误！");
        AdminInfoDto adminInfoDto = adminInfoToDto(adminInfo);
        add(adminInfoDto);
        return adminInfoDto;
    }

    public AdminInfoDto getOneByName(String name) {
        List<AdminInfoDto> collect = findAll().stream().filter(v -> v.getName().equals(name)).collect(Collectors.toList());
        if (!collect.isEmpty()) return collect.get(0);

        AdminInfo adminInfo = adminInfoDao.selectOne(Wrappers.<AdminInfo>lambdaQuery().eq(AdminInfo::getName, name));
        if (adminInfo == null) throw new RuntimeException("您输入的用户名有误！");
        AdminInfoDto dto = adminInfoToDto(adminInfo);
        add(dto);
        return dto;
    }

    @PostConstruct
    public void collect() {
        List<AdminInfo> adminInfos = adminInfoDao.selectList(null);
        if (adminInfos.isEmpty()) return;

        adminInfos.forEach(v -> add(adminInfoToDto(v)));
    }

    public AdminInfoDto adminInfoToDto(AdminInfo adminInfo) {
        AdminInfoDto dto = new AdminInfoDto();
        dto.setRoles(adminInfoDao.selectRoles(adminInfo.getId()));
        dto.setGroups(adminInfoDao.selectGroups(adminInfo.getId()));
        dto.setId(adminInfo.getId());
        dto.setName(adminInfo.getName());
        return dto;
    }

    public void delete(Long id) {
        super.deleteById(id);
        adminInfoDao.deleteById(id);
    }

    public void deleteByName(String name) {
        List<AdminInfoDto> collect = findAll().stream().filter(v -> v.getName().equals(name)).collect(Collectors.toList());
        if (!collect.isEmpty()) delete(collect.get(0).getId());
    }

}
