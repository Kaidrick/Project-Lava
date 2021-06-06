package moe.ofs.backend.pollservices;

import lombok.Getter;
import lombok.ToString;
import moe.ofs.backend.domain.dcs.poll.ExportObject;

@Getter
@ToString
public class DataUpdateBundle {

    String action;
    ExportObject data;
    boolean is_tail;
}
