package moe.ofs.backend;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.object.map.GeoPosition;
import moe.ofs.backend.object.map.GeoPositions;
import moe.ofs.backend.object.map.Orientation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
class GeoPositionsTest {
    GeoPosition geoPosition;
    GeoPosition compareGeoPosition;

    @BeforeEach
    void setUp() {
        geoPosition = new GeoPosition();
        geoPosition.setLatitude(64.698056);
        geoPosition.setLongitude(-110.609167);
        geoPosition.setAltitude(17);

        compareGeoPosition = GeoPositions.get(
                Orientation.NORTH, "64", "41", "53",
                Orientation.WEST, "110", "36", "33");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void latLon() {
//        System.out.println("GeoPositions.toLatLonDisplay(geoPosition) = " + GeoPositions.toLatLonDisplay(geoPosition));
//        System.out.println("GeoPositions.toLatLonAltDisplay(geoPosition) = " + GeoPositions.toLatLonAltDisplay(geoPosition));
    }

    @Test
    void formatStringArray() {
//        System.out.println(Arrays.toString(GeoPositions.formatStringArray(geoPosition, false)));
//        System.out.println(Arrays.toString(GeoPositions.formatStringArray(compareGeoPosition, false)));
    }


    void generator() {
// 需要构建一个 代码自动生成器 对象
        AutoGenerator mpg = new AutoGenerator();
// 配置策略
// 1、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("Tyler");
        gc.setOpen(false);
        gc.setFileOverride(false); // 是否覆盖
        gc.setServiceName("%sService"); // 去Service的I前缀
        gc.setMapperName("%sDao");
        gc.setIdType(IdType.ASSIGN_ID);
        gc.setDateType(DateType.ONLY_DATE);
//      是否启用swagger自动配置
//        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);
//2、设置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/lava?useUnicode=true&useSSL=false&autoReconnect=true&characterEnco" +
                "ding=utf-8&serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);
//3、包的配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("test");
        pc.setParent("moe.ofs");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("moe/ofs/backend/mybatis/service");
//        pc.setController("controller");
        mpg.setPackageInfo(pc);
//4、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(
                "export_object"
        );// 设置要映射的表名
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true); // 自动lombok；
//        strategy.setLogicDeleteFieldName("enable");
// 自动填充配置
        TableFill gmtCreate = new TableFill("create_time", FieldFill.INSERT);
        TableFill gmtModified = new TableFill("update_time",
                FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
//            tableFills.add(gmtCreate);
//            tableFills.add(gmtModified);
        strategy.setTableFillList(tableFills);
// 乐观锁
//            strategy.setVersionFieldName("version");
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        mpg.execute(); //执行

    }

}