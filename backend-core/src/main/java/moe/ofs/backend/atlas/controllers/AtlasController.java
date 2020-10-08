package moe.ofs.backend.atlas.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.atlas.services.AtlasService;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Controller
@RequestMapping("atlas")
public class AtlasController {

    private final AtlasService atlasService;

    public AtlasController(AtlasService atlasService) {
        this.atlasService = atlasService;
    }

    @RequestMapping(value = "{theater}/{level}/{x}/{y}",
            method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getTile(@PathVariable String theater, @PathVariable int level,
                   @PathVariable int x, @PathVariable int y) throws IOException {

        log.info("{}, {}, {}, {}", theater, level, x, y);

        // FIXME: bad idea to concat a path string from external input
//        InputStream in = getClass()
//                .getResourceAsStream(String.format("/data/atlas/nevada/%s_%d_%d_%d.png", theater, level, x, y));
//        return IOUtils.toByteArray(in);

        return atlasService.getTileImage(level, x, y);
    }
}
