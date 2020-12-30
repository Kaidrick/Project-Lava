package moe.ofs.backend.atlas.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

@Service
public class AtlasServiceImpl implements AtlasService {

    Map<String, byte[]> map;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void loadData() throws IOException, ClassNotFoundException {
        InputStream in = getClass()
                .getResourceAsStream("/data/atlas/atlas_20.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        map = (Map<String, byte[]>) objectInputStream.readObject();
    }

    @Override
    public byte[] getTileImage( //String theater,
                               int level, int x, int y) {
        return map.get(String.format("%s_%d_%d_%d", "Nevada", level, x, y));
    }
}
