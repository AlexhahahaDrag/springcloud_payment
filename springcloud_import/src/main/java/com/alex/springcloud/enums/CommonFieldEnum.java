package com.alex.springcloud.enums;

/**
 * @description: 公共字段枚举
 * @author:      alex
 * @createTime:  2020/11/18 19:43
 * @version:     1.0
 */
public enum CommonFieldEnum {

    S_KEY("s_key", "标准字段_代理键"),
    S_SRC("s_src", "标准字段_数据来源");

    CommonFieldEnum(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    private String code;

    private String comment;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
