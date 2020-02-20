package moe.ofs.backend.services;

import org.springframework.stereotype.Service;

@Service
public class ExportObjectServiceImpl implements ExportObjectService {
    @Override
    public String getInfo() {
        return "info!";
    }

    @Override
    public String getPosition() {
        return "position!";
    }
}
