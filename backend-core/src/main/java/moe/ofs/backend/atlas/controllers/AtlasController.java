package moe.ofs.backend.atlas.controllers;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.atlas.services.AtlasService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Slf4j
@Controller
//@Api(tags = "Atlas Map APIs for PIXI.js game map", value = "Provides endpoints for retrieving map data.")
@RequestMapping("atlas")
public class AtlasController {

    private final AtlasService atlasService;

    public AtlasController(AtlasService atlasService) {
        this.atlasService = atlasService;
    }

    //    @ApiOperation(value = "Retrieves map tile image by theater, level, x, and y")
    @RequestMapping(value = "{theater}/{level}/{x}/{y}",
            method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] getTile(
            @PathVariable
//            @ApiParam(value = "Game map theater name", example = "Nevada")
                    String theater,
            @PathVariable
//            @ApiParam(value = "Resolution level of tile", example = "20")
                    int level,
            @PathVariable
//            @ApiParam(value = "Zero-based x-th column from the upper left corner of the map")
                    int x,
            @PathVariable
//            @ApiParam(value = "Zero-based y-th row from the upper left corner of the map")
                    int y)
            throws IOException {

        log.info("{}, {}, {}, {}", theater, level, x, y);

        // FIXME: bad idea to concat a path string from external input
//        InputStream in = getClass()
//                .getResourceAsStream(String.format("/data/atlas/nevada/%s_%d_%d_%d.png", theater, level, x, y));
//        return IOUtils.toByteArray(in);

        return atlasService.getTileImage(level, x, y);
    }
}
