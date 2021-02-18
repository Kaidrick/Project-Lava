package moe.ofs.backend.function.weather.services.impl;

import moe.ofs.backend.domain.dcs.theater.Vector3D;
import moe.ofs.backend.function.weather.services.WeatherService;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Override
    public Object getWind(Vector3D point) {
        return null;
    }

    @Override
    public Object getTemperatureAndPressure(Vector3D point) {
        return null;
    }

    @Override
    public Object getAtmosphereInfo(Vector3D point) {
        return null;
    }
}
