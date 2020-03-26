package moe.ofs.backend.request;

import lombok.Getter;
import moe.ofs.backend.domain.ExportObject;

@Getter
public class DataUpdateBundle {

    String action;
    ExportObject data;
    boolean is_tail;
}
