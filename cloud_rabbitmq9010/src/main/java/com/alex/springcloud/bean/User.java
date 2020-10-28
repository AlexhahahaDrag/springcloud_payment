package com.alex.springcloud.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 *description:  用户类
 *author:       alex
 *createDate:   2020/10/28 22:32
 *version:      1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {

    private String name;

    private Integer age;

    private Integer version;
}
