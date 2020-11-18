package com.alex.springcloud.enums;

/**
 * @description: 拉链字段枚举
 * @author:      alex
 * @createTime:  2020/11/18 19:02
 * @version:     1.0
 */
public enum ZipperFieldEnum {

    S_START_TIME("s_start_time", "标准字段_拉链开始时间"),
    S_END_TIME("s_end_time", "标准字段_拉链结束时间"),
    S_STAT("s_stat", "标准字段_数据是否有效");

    ZipperFieldEnum(String code, String comment) {
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
