DROP TABLE IF EXISTS `sql_info`;
CREATE TABLE `sql_info`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `table_name_cn` varchar(255) COMMENT '表中文名',
                             `table_name` varchar(255) COMMENT '表名',
                             `ods_sql` text COMMENT 'ods maxcompute sql',
                             `dwd_zipper_table_name` varchar(255) COMMENT 'dwd全量表名',
                             `dwd_sql_zipper` text COMMENT 'dwd拉链SQL',
                             `dwd_add_table_name` varchar(255) COMMENT 'dwd增量表名',
                             `dwd_sql_add` text COMMENT 'dwd增量SQL',
                             `ods_to_dwd_init_sql` text COMMENT 'ods_to_dwd_init_sql',
                             `ods_to_dwd_sql` text COMMENT 'ods_to_dwd_sql',
                             `ods_sql_mysql` text COMMENT 'ods mysql sql',
                             `dwd_sql_zipper_mysql` text COMMENT 'dwd mysql zipper SQL',
                             `dwd_sql_add_mysql` text COMMENT 'dwd mysql add SQL',
                             `sys_code` varchar(255) COMMENT '系统编号',
                             PRIMARY KEY (`id`) USING BTREE
) COMMENT = 'sql信息表';


DROP TABLE IF EXISTS `sys_dict_t`;
CREATE TABLE `sys_dict_t`  (
                               `sys_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统编号',
                               `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
                               `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
