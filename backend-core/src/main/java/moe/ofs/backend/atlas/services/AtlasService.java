package moe.ofs.backend.atlas.services;

public interface AtlasService {
    byte[] getTileImage(// String theater,
                        int level, int x, int y);
}
