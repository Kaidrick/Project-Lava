package moe.ofs.backend.addons.services;

import moe.ofs.backend.Plugin;
import moe.ofs.backend.addons.model.PluginVo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddonRegistryServiceImpl implements AddonRegistryService {

    private final Map<Long, Plugin> map;  // in order to maintain an unique id for each plugin instead of ident

    protected Long getNextId() {
        return map.keySet().isEmpty() ? 1L : Collections.max(map.keySet()) + 1;
    }

    public AddonRegistryServiceImpl(List<Plugin> plugins) {
        map = new HashMap<>();
        plugins.forEach(plugin -> map.put(getNextId(), plugin));
    }

    @Override
    public List<PluginVo> findAll() {
        return map.entrySet().stream().map(entry -> {
            PluginVo pluginVo = new PluginVo();
            pluginVo.setId(entry.getKey());
            pluginVo.setIdent(entry.getValue().getFullName());
            pluginVo.setDescription(entry.getValue().getDescription());
            pluginVo.setName(entry.getValue().getName());

            return pluginVo;
        }).collect(Collectors.toList());
    }
}
