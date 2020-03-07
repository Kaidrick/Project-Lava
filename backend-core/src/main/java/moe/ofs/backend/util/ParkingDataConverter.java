package moe.ofs.backend.util;

import moe.ofs.backend.object.Parking;
import moe.ofs.backend.object.ParkingInfo;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParkingDataConverter {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String theaterName = "PersianGulf";

        try(InputStream inputStream = ClassLoader.class.getResourceAsStream("/data/" + theaterName + ".apron");
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            Object object = objectInputStream.readObject();

            if(object instanceof ArrayList) {
                List<Parking> list = (ArrayList<Parking>) object;

                List<ParkingInfo> parkingInfos = new ArrayList<>();
                for (Parking parking : list) {
                    ParkingInfo parkingInfo = ParkingInfo.builder().airdromeId(parking.getAirdromeId())
                            .airdromeName(parking.getAirdromeName())
                            .initialHeading(parking.getInitialHeading())
                            .northCorrection(parking.getNorthCorrection())
                            .parkingId(parking.getId())
                            .position(parking.getPosition())
                            .terminalType(parking.getTerminalType()).build();

                    System.out.println(parkingInfo);

                    parkingInfos.add(parkingInfo);
                }

                // after populating parkingInfos
                Path path = Paths.get("backend-core/src/main/resources/data").resolve(theaterName + ".data");
                FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(parkingInfos);
                fileOutputStream.close();
            }
        }
    }
}
