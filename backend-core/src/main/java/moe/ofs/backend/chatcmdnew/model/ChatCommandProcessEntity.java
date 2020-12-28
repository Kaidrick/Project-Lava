package moe.ofs.backend.chatcmdnew.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.ofs.backend.hookinterceptor.HookProcessEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatCommandProcessEntity extends HookProcessEntity {
    private String message;
    private String keyword;
}
