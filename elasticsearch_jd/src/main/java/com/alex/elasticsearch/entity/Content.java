package com.alex.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Content {

    private String image;

    private String price;

    private String name;
}
