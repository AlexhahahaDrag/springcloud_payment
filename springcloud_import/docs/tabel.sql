
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sql_info
-- ----------------------------
DROP TABLE IF EXISTS `sql_info`;
CREATE TABLE `sql_info`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `table_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表中文名',
                             `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
                             `ods_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ods maxcompute sql',
                             `dwd_zipper_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'dwd全量表名',
                             `dwd_sql_zipper` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dwd拉链SQL',
                             `dwd_add_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'dwd增量表名',
                             `dwd_sql_add` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dwd增量SQL',
                             `ods_to_dwd_init_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ods_to_dwd_init_sql',
                             `ods_to_dwd_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ods_to_dwd_sql',
                             `version` int(255) NULL DEFAULT NULL COMMENT '版本号',
                             `ods_sql_mysql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'ods mysql sql',
                             `dwd_sql_zipper_mysql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dwd mysql zipper SQL',
                             `dwd_sql_add_mysql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dwd mysql add SQL',
                             `is_fact` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是事实表(1:是,0:否)',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1305 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sql信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
