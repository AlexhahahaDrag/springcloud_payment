package com.alex.springcloud.constants;

import com.alex.springcloud.enums.AddFieldEnum;
import com.alex.springcloud.enums.CommonFieldEnum;
import com.alex.springcloud.enums.ZipperFieldEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 字段常量
 * @author:      alex
 * @createTime:  2020/11/17 16:41
 * @version:     1.0
 */
public class SystemConstant {

    //字段常量
    //拉链表包含字段
    public static Map<String, String> DWD_COMMON_TABLE = new HashMap<>();

    //dwd拉链表包含字段
    public static Map<String, String> DWD_ZIPPER_TABLE = new HashMap<>();

    //dwd增量表包含字段
    public static Map<String, String> DWD_ADD_TABLE = new HashMap<>();

    static {
        DWD_COMMON_TABLE.put(CommonFieldEnum.S_SRC.getCode(), CommonFieldEnum.S_SRC.getComment());
        DWD_ADD_TABLE.put(AddFieldEnum.S_ACTION.getCode(), AddFieldEnum.S_ACTION.getComment());
        DWD_ZIPPER_TABLE.put(ZipperFieldEnum.S_STAT.getCode(), ZipperFieldEnum.S_STAT.getComment());
        DWD_ZIPPER_TABLE.put(ZipperFieldEnum.S_START_TIME.getCode(), ZipperFieldEnum.S_START_TIME.getComment());
        DWD_ZIPPER_TABLE.put(ZipperFieldEnum.S_END_TIME.getCode(), ZipperFieldEnum.S_END_TIME.getComment());
    }

    //sql常量
    //增量
    public static final String ADD_TYPE = "add";

    //拉链
    public static final String ZIPPER_TYPE = "zipper";

    //普通
    public static final String NOR_TYPE = "nor";

    //mysql数据库sql
    public static final String MYSQL_TYPE = "mysql";

    //maxCompute sql
    public static final String MAX_COMPUTE = "max_compute";

    //表更新类型
    public static final String ADD_CN = "增量";

    public static final String FULL_CN = "全量";

    public static final String ADD_TABLE = "i";

    public static final String FULL_TABLE = "f";

    //表层级
    public static final String ODS = "ods";

    public static final String DWD = "dwd";

    public static final String DWD_UP = "DWD";

    public static final String DIM = "dim";

    public static final String DIM_UP = "DIM";

    public static final String DWS = "dws";

    public static final String ADS = "ads";
}
