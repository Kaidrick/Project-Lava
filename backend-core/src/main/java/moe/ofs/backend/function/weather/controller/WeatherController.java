package moe.ofs.backend.function.weather.controller;

import moe.ofs.backend.domain.dcs.theater.Vector3D;
import moe.ofs.backend.function.weather.services.WeatherService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("wx")
@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RequestMapping("info")
    Object getWeather(@RequestBody Vector3D point) {
        return weatherService.getAtmosphereInfo(point);
    }
}
