package com.alex.springcloud.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 字典导入信息
 * @author:      alex
 * @createTime:  2021/3/5 9:34
 * @version:     1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "DictImportInfo", description = "字典导入信息")
public class DictImportInfo {

    @Excel(name = "名称")
    private String clazzName;

    @Excel(name = "代码")
    private String clazzCode;

    private String dictName;

    private String dictCode;

    @Excel(name = "说明")
    private String description;

    private String operateTime;
}
