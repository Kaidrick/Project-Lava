package moe.ofs.backend.gui;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BanPlayerOptionDialogResult {
    private String reason;
    private int days;
    private int hours;
    private int minutes;
    private boolean permanent;
    private LocalDate date;

    public BanPlayerOptionDialogResult(String reason, String days, String hours, String minutes,
                                       boolean permanent, LocalDate date) {
        this.reason = reason;
        this.days = days.equals("") ? 0 : Integer.parseInt(days);
        this.hours = hours.equals("") ? 0 : Integer.parseInt(hours);
        this.minutes = minutes.equals("") ? 0 : Integer.parseInt(minutes);
        this.permanent = permanent;
        this.date = this.days == 0 && this.hours == 0 && this.minutes == 0 ? date : null;
    }
}
