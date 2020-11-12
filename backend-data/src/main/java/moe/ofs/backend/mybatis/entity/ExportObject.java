package moe.ofs.backend.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Tyler
 * @since 2020-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
public class ExportObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long runtimeId;

    private Double ownBank;

    private String coalition;

    private Integer coalitionId;

    private Integer countryId;

    private String groupName;

    private Double ownHeading;

    private String ownName;

    private Double ownPitch;

    private String unitName;

    private Double geoAltitude;

    private Double geoLatitude;

    private Double geoLongitude;

    private Double vectorX;

    private Double vectorY;

    private Double vectorZ;


}
