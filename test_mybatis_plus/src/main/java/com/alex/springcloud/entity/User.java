package com.alex.springcloud.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_info")
public class User {

    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @TableLogic
    private Integer isDelete;
}
