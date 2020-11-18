package com.alex.springcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sql_info")
@ApiModel(value = "SqlInfo", description = "SqlInfo类")
public class SqlInfo {

    @ApiModelProperty(value = "id", name = "id", example = "1")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "type", name = "类型", example = "ods")
    @TableField(value = "type")
    private String type;

    @ApiModelProperty(value = "tableName", name = "表名", example = "ods_table_name")
    @TableField(value = "table_name")
    private String tableName;

    @ApiModelProperty(value = "tableNameCn", name = "表中文名", example = "ods_表")
    @TableField(value = "table_name_cn")
    private String tableNameCn;

    @ApiModelProperty(value = "sqlZipper", name = "拉链表sql", example = "1")
    @TableField(value = "sql_zipper")
    private String sqlZipper;

    @ApiModelProperty(value = "sqlAdd", name = "增量表sql", example = "1")
    @TableField(value = "sql_add")
    private String sqlAdd;

    @ApiModelProperty(value = "isFact", name = "是否是事实表1:是 0:否", example = "1")
    @TableField(value = "is_fact")
    private String isFact;

    @ApiModelProperty(value = "sqlAddMysql", name = "mysql add sql", example = "1")
    @TableField(value = "sql_add_mysql")
    private String sqlAddMysql;

    @ApiModelProperty(value = "sqlZipperMysql", name = "mysql zipper sql", example = "1")
    @TableField(value = "sql_zipper_mysql")
    private String sqlZipperMysql;
//    @ApiModelProperty(value = "isMust", name = "是否必填", example = "是")
//    @TableField(value = "is_must")
//    private String isMust;
//
//    @ApiModelProperty(value = "originSchemaTable", name = "原始数据库表", example = "N_BIZ_T_CTI_ZX_TM")
//    @TableField(value = "origin_scheme_table")
//    private String originSchemaTable;
//
//    @ApiModelProperty(value = "originSchemaColumn", name = "原始数据库字段", example = "AGENT_ID")
//    @TableField(value = "origin_schema_column")
//    private String originSchemaColumn;
//
//    @ApiModelProperty(value = "remark", name = "备注", example = "haha")
//    @TableField(value = "remark")
//    private String remark;

    @ApiModelProperty(value = "version", name = "版本号", example = "1")
    @TableField(value = "version")
    private Integer version;
}
