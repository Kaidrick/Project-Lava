package moe.ofs.backend.common;


/**
 * StaticService represent the service for data which will not change during dcs runtime.
 * The data is loaded only once at mission start and will be discarded after mission ends.
 */
public interface StaticService {

    void loadData();

}
