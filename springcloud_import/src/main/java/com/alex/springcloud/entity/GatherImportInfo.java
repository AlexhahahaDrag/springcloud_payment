package com.alex.springcloud.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "GatherImportInfo", description = "首页信息")
public class GatherImportInfo {

    @Excel(name = "表中文名")
    private String tableNameCn;

    @Excel(name = "表英文名")
    private String tableName;

    @Excel(name = "原始数据库表名")
    private String databaseTableName;

    @Excel(name = "表类型")
    private String tableType;

    @Excel(name = "同步方式")
    private String sysnWay;

    @Excel(name = "是否物理删除")
    private String isPhysicsDelete;

    @Excel(name = "能否识别出增量数据")
    private String isAddData;

    @Excel(name = "创建时间字段")
    private String createTime;

    @Excel(name = "修改时间字段")
    private String updateTime;

    @Excel(name = "dwd表号")
    private String dwdTableNo;

    @Excel(name = "同步频率")
    private String sysnRate;
}
