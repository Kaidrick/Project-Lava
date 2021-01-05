package moe.ofs.backend.services;

import moe.ofs.backend.object.ParkingInfo;

import java.util.List;
import java.util.Optional;

public interface ParkingInfoService extends StaticService, MissionPersistenceRepository {

    Optional<ParkingInfo> getParking(int airdromeId, int parkingId);

    List<ParkingInfo> getAllParking();

    void dispose();
}
