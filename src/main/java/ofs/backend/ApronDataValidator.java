package ofs.backend;

import ofs.backend.core.object.Parking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class ApronDataValidator {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        InputStream inputStream = ApronDataValidator.class.getResourceAsStream("Nevada.apron");
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        if(object instanceof ArrayList) {
            List<Parking> list = (ArrayList<Parking>) object;

            list.forEach(p -> {
                double heading = p.getReadableHeading();
                double inLine = p.getNorthCorrection() >=0 ? Math.toDegrees(p.getNorthCorrection())
                        : Math.toDegrees(2 * Math.PI + p.getNorthCorrection());
                if(Math.abs(heading - inLine) < 0.4) {
                    System.out.println(p);
                }
            });
        }
    }
}
