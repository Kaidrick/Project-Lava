package moe.ofs.backend.services;

import moe.ofs.backend.object.ParkingInfo;

import java.util.Optional;

public interface ParkingInfoService extends StaticService {

    Optional<ParkingInfo> getParking(int airdromeId, int parkingId);

    void dispose();
}
