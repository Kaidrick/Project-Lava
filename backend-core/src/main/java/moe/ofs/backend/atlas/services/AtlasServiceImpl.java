package moe.ofs.backend.atlas.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

@Service
public class AtlasServiceImpl implements AtlasService {

    Object[][] objects;

    @PostConstruct
    public void loadData() throws IOException, ClassNotFoundException {
        InputStream in = getClass()
                .getResourceAsStream("/data/atlas/atlas_20");
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        objects = (Object[][]) objectInputStream.readObject();
    }

    @Override
    public byte[] getTileImage( //String theater,
                               int level, int x, int y) {
        return (byte[]) objects[x][y];
    }
}
