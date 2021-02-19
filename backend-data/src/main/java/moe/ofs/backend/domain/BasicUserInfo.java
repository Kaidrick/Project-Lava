package moe.ofs.backend.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BasicUserInfo implements Serializable {
    private static final long serialVersionUID = -100L;
    private String name;
    private String password;
    private List<String> roles;
}
