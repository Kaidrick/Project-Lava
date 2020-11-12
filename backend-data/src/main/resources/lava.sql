SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for export_object
-- ----------------------------
DROP TABLE IF EXISTS `export_object`;
CREATE TABLE `export_object`  (
  `runtime_id` bigint(20) UNSIGNED NOT NULL,
  `own_bank` double NULL DEFAULT NULL,
  `coalition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `coalition_id` int(11) NULL DEFAULT NULL,
  `country_id` int(11) NULL DEFAULT NULL,
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `own_heading` double NULL DEFAULT NULL,
  `own_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `own_pitch` double NULL DEFAULT NULL,
  `unit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `geo_altitude` double NOT NULL,
  `geo_latitude` double NOT NULL,
  `geo_longitude` double NOT NULL,
  `vector_x` double NOT NULL,
  `vector_y` double NOT NULL,
  `vector_z` double NOT NULL,
  PRIMARY KEY (`runtime_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lava_system_log
-- ----------------------------
DROP TABLE IF EXISTS `lava_system_log`;
CREATE TABLE `lava_system_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1562 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_info
-- ----------------------------
DROP TABLE IF EXISTS `player_info`;
CREATE TABLE `player_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ipaddr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `lang` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `net_id` int(11) NOT NULL,
  `pilot_id` bigint(20) NOT NULL,
  `ping` int(11) NOT NULL,
  `side` int(11) NOT NULL,
  `slot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `started` bit(1) NOT NULL,
  `ucid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
