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

    @ApiModelProperty(value = "odsSql", name = "ods sql", example = "1")
    @TableField(value = "ods_sql")
    private String odsSql;

    @ApiModelProperty(value = "odsSqlMysql", name = "ods sql mysql", example = "1")
    @TableField(value = "ods_sql_mysql")
    private String odsSqlMysql;

    @ApiModelProperty(value = "dwdSqlZipper", name = "dwd拉链表sql", example = "1")
    @TableField(value = "dwd_sql_zipper")
    private String dwdSqlZipper;

    @ApiModelProperty(value = "dwdSqlAdd", name = "dwd增量表sql", example = "1")
    @TableField(value = "dwd_sql_add")
    private String dwdSqlAdd;

    @ApiModelProperty(value = "isFact", name = "是否是事实表1:是 0:否", example = "1")
    @TableField(value = "is_fact")
    private String isFact;

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

    @ApiModelProperty(value = "version", name = "版本号", example = "1")
    @TableField(value = "version")
    private Integer version;
}
