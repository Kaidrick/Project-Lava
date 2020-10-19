/*
    数据库类型：Mysql
    库名：     lava
    字符集：    utf8mb4
    排序规则：  utf8mb4_general_ci
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for EXPORT_OBJECTS
-- ----------------------------
DROP TABLE IF EXISTS `EXPORT_OBJECTS`;
CREATE TABLE `EXPORT_OBJECTS`
(
    `ID`             bigint(20) NOT NULL AUTO_INCREMENT,
    `OWN_BANK`       double       DEFAULT NULL,
    `COALITION`      varchar(255) DEFAULT NULL,
    `COALITION_ID`   int(11)      DEFAULT NULL,
    `COUNTRY_ID`     int(11)      DEFAULT NULL,
    `GROUP_NAME`     varchar(255) DEFAULT NULL,
    `OWN_HEADING`    double       DEFAULT NULL,
    `OWN_NAME`       varchar(255) DEFAULT NULL,
    `OWN_PITCH`      double       DEFAULT NULL,
    `RUNTIME_ID`     bigint(20)   DEFAULT NULL,
    `UNIT_NAME`      varchar(255) DEFAULT NULL,
    `GEOPOSITION_ID` bigint(20)   DEFAULT NULL,
    `VECTOR3D_ID`    bigint(20)   DEFAULT NULL,
    PRIMARY KEY (`ID`),
    KEY `FKANC1RNXR5CQG9TV2RTHQ8OP54` (`VECTOR3D_ID`),
    KEY `FKCYL3C74M22PJ5B9TR5EOD81N8` (`GEOPOSITION_ID`),
    CONSTRAINT `FKANC1RNXR5CQG9TV2RTHQ8OP54` FOREIGN KEY (`VECTOR3D_ID`) REFERENCES `VECTOR3D` (`ID`),
    CONSTRAINT `FKCYL3C74M22PJ5B9TR5EOD81N8` FOREIGN KEY (`GEOPOSITION_ID`) REFERENCES `GEO_POSITION` (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for FLAGS
-- ----------------------------
DROP TABLE IF EXISTS `FLAGS`;
CREATE TABLE `FLAGS`
(
    `ID`    bigint(20) NOT NULL,
    `VALUE` bit(1)       DEFAULT NULL,
    `KEY`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ID`),
    CONSTRAINT `FK8VTLCQV7NGXTMI2VV23MY9TOX` FOREIGN KEY (`ID`) REFERENCES `EXPORT_OBJECTS` (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for GEO_POSITION
-- ----------------------------
DROP TABLE IF EXISTS `GEO_POSITION`;
CREATE TABLE `GEO_POSITION`
(
    `ID`        bigint(20) NOT NULL AUTO_INCREMENT,
    `ALTITUDE`  double     NOT NULL,
    `LATITUDE`  double     NOT NULL,
    `LONGITUDE` double     NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for LAVA_SYSTEM_LOG
-- ----------------------------
DROP TABLE IF EXISTS `LAVA_SYSTEM_LOG`;
CREATE TABLE `LAVA_SYSTEM_LOG`
(
    `ID`          int(11)      NOT NULL,
    `NAME`        varchar(255) NOT NULL,
    `MESSAGE`     varchar(255) NOT NULL,
    `SOURCE_NAME` varchar(255) NOT NULL,
    `LEVEL`       varchar(64)  NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for PLAYER_INFO
-- ----------------------------
DROP TABLE IF EXISTS `PLAYER_INFO`;
CREATE TABLE `PLAYER_INFO`
(
    `ID`       bigint(20) NOT NULL AUTO_INCREMENT,
    `IPADDR`   varchar(255) DEFAULT NULL,
    `LANG`     varchar(255) DEFAULT NULL,
    `NAME`     varchar(255) DEFAULT NULL,
    `NET_ID`   int(11)    NOT NULL,
    `PILOT_ID` bigint(20) NOT NULL,
    `PING`     int(11)    NOT NULL,
    `SIDE`     int(11)    NOT NULL,
    `SLOT`     varchar(255) DEFAULT NULL,
    `STARTED`  tinyint(1) NOT NULL,
    `UCID`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for STUDENT
-- ----------------------------
DROP TABLE IF EXISTS `STUDENT`;
CREATE TABLE `STUDENT`
(
    `ID`              int(11)      NOT NULL,
    `NAME`            varchar(255) NOT NULL,
    `PASSPORT_NUMBER` varchar(255) NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for TYPE
-- ----------------------------
DROP TABLE IF EXISTS `TYPE`;
CREATE TABLE `TYPE`
(
    `ID`    bigint(20) NOT NULL,
    `VALUE` int(11)      DEFAULT NULL,
    `KEY`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ID`),
    CONSTRAINT `FKRNY5YSTALBA6TLJD0HJ9AB8JR` FOREIGN KEY (`ID`) REFERENCES `EXPORT_OBJECTS` (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- ----------------------------
-- Table structure for VECTOR3D
-- ----------------------------
DROP TABLE IF EXISTS `VECTOR3D`;
CREATE TABLE `VECTOR3D`
(
    `ID` bigint(20) NOT NULL AUTO_INCREMENT,
    `X`  double     NOT NULL,
    `Y`  double     NOT NULL,
    `Z`  double     NOT NULL,
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
