/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : dataworks

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 18/11/2020 15:34:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sql_info
-- ----------------------------
DROP TABLE IF EXISTS `sql_info`;
CREATE TABLE `sql_info`  (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
                             `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
                             `sql_zipper` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '拉链SQL',
                             `version` int(255) NULL DEFAULT NULL COMMENT '版本号',
                             `table_name_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表中文名',
                             `sql_add` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '增量SQL',
                             `is_fact` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是事实表(0:是,1:否)',
                             `sql_add_mysql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'mysql add SQL',
                             `sql_zipper_mysql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'mysql zipper SQL',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 657 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'sql信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
