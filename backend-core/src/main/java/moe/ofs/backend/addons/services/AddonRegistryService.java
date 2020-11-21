package moe.ofs.backend.addons.services;

import moe.ofs.backend.addons.model.PluginVo;

import java.util.List;

public interface AddonRegistryService {
    List<PluginVo> findAll();
}
