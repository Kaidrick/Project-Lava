package moe.ofs.backend.object;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimObject implements Serializable {

    protected int     id;
    protected String  name;
    protected int     category;

}
