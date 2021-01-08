package moe.ofs.backend.discipline.model;

import lombok.Data;

import java.time.Duration;

@Data
public class PlayerDisciplinaryAction {
    private String ucid;
    private Punishment punishment;
    private Duration duration;
    private String reason;  // the reason provided to a banned player when s/he tries to connect to server
    private String remark;  // a remark field that can be used to take additional notes
}
