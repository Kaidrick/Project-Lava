package moe.ofs.backend.function.weather.services;

import moe.ofs.backend.domain.dcs.theater.Vector3D;

public interface WeatherService {
    Object getWind(Vector3D point);

    Object getTemperatureAndPressure(Vector3D point);

    Object getAtmosphereInfo(Vector3D point);
}
