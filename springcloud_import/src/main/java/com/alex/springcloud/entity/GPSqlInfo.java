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

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "gp_sql_info")
@ApiModel(value = "GPSqlInfo", description = "GPSqlInfo类")
public class GPSqlInfo {

    @ApiModelProperty(value = "id", name = "id", example = "1")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "tableName", name = "表名", example = "ods_table_name")
    @TableField(value = "table_name")
    private String tableName;

    @ApiModelProperty(value = "tableSql", name = "建表语句", example = "table_sql")
    @TableField(value = "table_sql")
    private String tableSql;

    @ApiModelProperty(value = "belongTo", name = "属于", example = "lihua")
    @TableField(value = "belong_to")
    private String belongTo;

    @TableField(value = "columnList", exist = false)
    @ApiModelProperty(value = "columnList", name = "字段列", example = "aa")
    List<GPSqlInfoImport> columnList;
}
