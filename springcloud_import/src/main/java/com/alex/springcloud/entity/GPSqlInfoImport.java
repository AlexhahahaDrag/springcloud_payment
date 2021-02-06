package com.alex.springcloud.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPSqlInfoImport {

    private String tableName;

    @Excel(name = "字段名")
    private String column;

    @Excel(name = "数据类型")
    private String columnType;

    @Excel(name = "字段类型")
    private String columnName;

    @Excel(name = "来源指标")
    private String source;

    @Excel(name = "说明")
    private String remark;
}
