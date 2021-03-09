package com.alex.springcloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.classgraph.json.Id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 字典类
 * @author:      alex
 * @createTime:  2021/3/5 9:33
 * @version:     1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "dict_info")
@ApiModel(value = "DictInfoBo", description = "DictInfoBo类")
public class DictInfoBo {

    @ApiModelProperty(value = "id", name = "id", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "code", name = "字典数据", example = "01")
    @TableField(value = "code")
    private String code;

    @ApiModelProperty(value = "belongTo", name = "归属", example = "1")
    @TableField(value = "belong_to")
    private String belongTo;

    @ApiModelProperty(value = "sysCode", name = "系统号", example = "1")
    @TableField(value = "sys_code")
    private String sysCode;


}
