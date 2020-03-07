package moe.ofs.backend.util;

import moe.ofs.backend.object.Parking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class ApronDataValidator {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try(InputStream inputStream = ClassLoader.class.getResourceAsStream("/data/obsolete/Nevada.apron");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object object = objectInputStream.readObject();

            if(object instanceof ArrayList) {
                List<Parking> list = (ArrayList<Parking>) object;
                list.forEach(System.out::println);
            }
        }
    }
}
