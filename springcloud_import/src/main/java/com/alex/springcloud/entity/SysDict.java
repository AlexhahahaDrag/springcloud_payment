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
@TableName(value = "sys_dict_t")
@ApiModel(value = "SysDict", description = "SysDict字典类")
public class SysDict {

    @ApiModelProperty(value = "sysCode", name = "系统编码", example = "302")
    @TableField(value = "sys_code")
    private String sysCode;

    @ApiModelProperty(value = "code", name = "编码", example = "sys_code")
    @TableField(value = "code")
    private String code;

    @ApiModelProperty(value = "value", name = "值", example = "01,02")
    @TableField(value = "value")
    private String value;
}
