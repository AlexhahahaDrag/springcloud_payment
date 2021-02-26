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

    @ApiModelProperty(value = "tableName", name = "表名", example = "ods_table_name")
    @TableField(value = "table_name")
    private String tableName;

    @ApiModelProperty(value = "tableNameCn", name = "表中文名", example = "ods_表")
    @TableField(value = "table_name_cn")
    private String tableNameCn;

    @ApiModelProperty(value = "odsSql", name = "ods sql", example = "1")
    @TableField(value = "ods_sql")
    private String odsSql;

    @ApiModelProperty(value = "odsSqlMysql", name = "ods sql mysql", example = "1")
    @TableField(value = "ods_sql_mysql")
    private String odsSqlMysql;

    @ApiModelProperty(value = "dwdZipperTableName", name = "ods sql", example = "1")
    @TableField(value = "dwd_zipper_table_name")
    private String dwdZipperTableName;

    @ApiModelProperty(value = "dwdSqlZipper", name = "dwd拉链表sql", example = "1")
    @TableField(value = "dwd_sql_zipper")
    private String dwdSqlZipper;

    @ApiModelProperty(value = "dwdAddTableName", name = "ods sql", example = "1")
    @TableField(value = "dwd_add_table_name")
    private String dwdAddTableName;

    @ApiModelProperty(value = "dwdSqlAdd", name = "dwd增量表sql", example = "1")
    @TableField(value = "dwd_sql_add")
    private String dwdSqlAdd;

    @ApiModelProperty(value = "dwdSqlAddMysql", name = "dwd mysql add sql", example = "1")
    @TableField(value = "dwd_sql_add_mysql")
    private String dwdSqlAddMysql;

    @ApiModelProperty(value = "dwdSqlZipperMysql", name = "dwd mysql zipper sql", example = "1")
    @TableField(value = "dwd_sql_zipper_mysql")
    private String dwdSqlZipperMysql;

    @ApiModelProperty(value = "odsToDwdInitSql", name = "ods转dwd init sql", example = "1")
    @TableField(value = "ods_to_dwd_init_sql")
    private String odsToDwdInitSql;

    @ApiModelProperty(value = "odsToDwdSql", name = "ods转dwd sql", example = "1")
    @TableField(value = "ods_to_dwd_sql")
    private String odsToDwdSql;

    @ApiModelProperty(value = "sysCode", name = "系统编号", example = "302")
    @TableField(value = "sys_code")
    private String sysCode;

    @ApiModelProperty(value = "belongTo", name = "属于", example = "lihua")
    @TableField(value = "belong_to")
    private String belongTo;

    @ApiModelProperty(value = "odsTestSql", name = "ods测试sql", example = "")
    @TableField(value = "ods_test_sql")
    private String odsTestSql;

    @ApiModelProperty(value = "sqlZipperGreenplumName", name = "greenplum zipper sql name", example = "1")
    @TableField(value = "sql_zipper_greenplum_name")
    private String sqlZipperGreenplumName;

    @ApiModelProperty(value = "sqlZipperGreenplum", name = "greenplum zipper sql", example = "1")
    @TableField(value = "sql_zipper_greenplum")
    private String sqlZipperGreenplum;

    @ApiModelProperty(value = "oldOdsToOldSql", name = "old Ods To Old Sql", example = "1")
    @TableField(value = "old_ods_to_ods_sql")
    private String oldOdsToOldSql;
}
