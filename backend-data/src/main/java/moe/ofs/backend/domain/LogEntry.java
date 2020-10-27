package moe.ofs.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import moe.ofs.backend.object.LogLevel;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@Accessors(chain = true)
@TableName("lava_system_log")
public class LogEntry implements Serializable {
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("time")
    private String time;

    @TableField("level")
    private LogLevel logLevel;

    @TableField("message")
    private String message;

    @TableField("source")
    private String source;

    @Override
    public String toString() {
        return String.format("%s %s %s",
//                time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                time,
                logLevel.name(),
                message);
    }
}
