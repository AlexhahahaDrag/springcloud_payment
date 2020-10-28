package com.alex.springcloud.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *description:  客户类
 *author:       alex
 *createDate:   2020/10/28 22:32
 *version:      1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Customer {

    //客户名字
    private String name;

    //描述
    private String description;

    private Integer version;
}
