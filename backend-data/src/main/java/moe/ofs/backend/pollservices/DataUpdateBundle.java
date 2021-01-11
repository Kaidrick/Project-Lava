package moe.ofs.backend.pollservices;

import lombok.Getter;
import moe.ofs.backend.domain.dcs.poll.ExportObject;

@Getter
public class DataUpdateBundle {

    String action;
    ExportObject data;
    boolean is_tail;
}
