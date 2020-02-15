package ofs.backend.core.request;

abstract class RequestToMission extends BaseRequest {
    RequestToMission() {
        super(Level.MISSION);
    }
}
