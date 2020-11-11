package com.alex.springcloud.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlInfoImport {

    @Excel(name = "ODS标准层表名")
    private String tableName;

    private String tableNameCn;

    @Excel(name = "ODS标准层表字段英文名")
    private String column;

    @Excel(name = "ODS标准层表字段中文名")
    private String columnName;

    private String columnType;

    @Excel(name = "是否必填")
    private String isMust;

    @Excel(name = "原始数据库表名")
    private String originSchemaTable;

    @Excel(name = "原始数据库字段名")
    private String originSchemaColumn;

    @Excel(name = "备注")
    private String remark;
}
