package moe.ofs.backend.atlas.services;

import moe.ofs.backend.atlas.exceptions.AtlasBorderOutOfBoundException;

public interface AtlasService {
    byte[] getTileImage(// String theater,
                        int level, int x, int y) throws AtlasBorderOutOfBoundException;
}
