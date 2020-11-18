package com.alex.springcloud.enums;


/**
 * @description: 增量字段枚举
 * @author:      alex
 * @createTime:  2020/11/18 18:54
 * @version:     1.0
 */
public enum AddFieldEnum {
    S_ACTION("s_action", "标准字段_操作类型（his,insert,update,delete）");

    AddFieldEnum(String code, String comment) {
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
