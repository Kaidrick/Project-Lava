package moe.ofs.backend.object.map;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ReferencePoint {
    @SerializedName("callsignStr")
    private String name;

    @SerializedName("comment")
    private String description;

    private long id;

    private double x;

    private double y;

    private Map<String, Number> properties;
}
