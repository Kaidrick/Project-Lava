package moe.ofs.backend.chatcmdnew.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.ofs.backend.hookinterceptor.HookProcessEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatCommandProcessEntity extends HookProcessEntity {
    @SerializedName("msg")
    private String message;

    @SerializedName("kw")
    private String keyword;
}
