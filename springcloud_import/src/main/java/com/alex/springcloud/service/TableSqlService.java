package com.alex.springcloud.service;

import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.enums.CommonFieldEnum;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 生成创建table sql语句
 * @author:      alex
 * @createTime:  2020/11/23 16:59
 * @version:     1.0
 */
@Service
public class TableSqlService {

    /**
     * @param list           列数据
     * @param tableName      表明
     * @param tableNameCn    表中文名
     * @param type           类型（拉链）
     * @param database       数据库类型（mysql，max_compute）
     * @param level           层级（ods,dwd,dws,ads）
     * @param isZ            是否是拉链表
     * @description:         生成创建表的sql语句
     * @author: alex
     * @return: java.lang.String
     */
    public String setSql(List<SqlInfoImport> list, String tableName, String tableNameCn, String type, String database, String level, Integer isZ) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        stringBuilder.append(tableName +"` (");
        if (SystemConstant.DWD.equals(level)) {
            stringBuilder.append("`" + CommonFieldEnum.S_KEY.getCode() + "` ");
            stringBuilder.append("string");
            stringBuilder.append(" COMMENT '" + CommonFieldEnum.S_KEY.getComment() + "',");
        }
        for (SqlInfoImport sqlInfoImport : list) {
            stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
            stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
            stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
        }
        if (SystemConstant.ADD_TYPE.equals(type)) {
            for(Map.Entry entry : SystemConstant.DWD_ADD_TABLE.entrySet()) {
                stringBuilder.append("`" + entry.getKey() + "` ");
                stringBuilder.append("string");
                stringBuilder.append(" COMMENT '" + entry.getValue() + "',");
            }
        }
        if (SystemConstant.ZIPPER_TYPE.equals(type)) {
            for(Map.Entry entry : SystemConstant.DWD_ZIPPER_TABLE.entrySet()) {
                stringBuilder.append("`" + entry.getKey() + "` ");
                stringBuilder.append("string");
                stringBuilder.append(" COMMENT '" + entry.getValue() + "',");
            }
        }
        if (SystemConstant.DWD.equals(level)) {
            for (Map.Entry entry : SystemConstant.DWD_COMMON_TABLE.entrySet()) {
                stringBuilder.append("`" + entry.getKey() + "` ");
                stringBuilder.append("string");
                stringBuilder.append(" COMMENT '" + entry.getValue() + "',");
            }
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        if (SystemConstant.MAX_COMPUTE.equals(database)) {
            stringBuilder.append(") COMMENT '" + tableNameCn + "'");
            stringBuilder.append(" PARTITIONED BY (ds string COMMENT '业务日期');");
        }
        if (SystemConstant.MYSQL_TYPE.equals(database)) {
            stringBuilder.append(") COMMENT = '" + tableNameCn + "';");
        }
        return stringBuilder.toString();
    }
}
