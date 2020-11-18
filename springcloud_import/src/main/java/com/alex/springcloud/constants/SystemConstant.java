package com.alex.springcloud.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * @description: 字段常量
 * @author:      alex
 * @createTime:  2020/11/17 16:41
 * @version:     1.0
 */
public class SystemConstant {

    //字段常量
    //拉链表不包含字段
    public static Set<String> ZIPPER_TABLE = new HashSet<>();

    public static String S_START_TIME = "s_start_time";

    public static String S_END_TIME = "s_end_time";

    public static String S_STAT = "s_stat";

    public static String S_ACTION = "s_action";

    //增量表不包含字段
    public static Set<String> ADD_TABLE = new HashSet<>();

    static {
        ADD_TABLE.add(S_START_TIME);
        ADD_TABLE.add(S_END_TIME);
        ADD_TABLE.add(S_STAT);
        ZIPPER_TABLE.add(S_ACTION);
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

    public static final String ADD_CN = "增量";

    public static final String FULL_CN = "全量";
}
