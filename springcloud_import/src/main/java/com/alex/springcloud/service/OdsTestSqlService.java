package com.alex.springcloud.service;

import com.alex.springcloud.entity.SqlInfoImport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @description: 生成ods测试数据sql
 * @author:      alex
 * @createTime:  2020/12/22 16:29
 * @version:     1.0
 */
@Service
public class OdsTestSqlService {

    public String setOdsTestSql(List<SqlInfoImport> list, String odsTableName) {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String time = "yyyyMMddhhmmss";
        String date = "yyyyMMdd";
        sb.append(" INSERT INTO TABLE ");
        sb.append(odsTableName);
        sb.append(" PARTITION(ds='" + DateTimeFormatter.ofPattern(date).format(now) + "')");
        sb.append("VALUES( ");
        int id = 0;
        for (SqlInfoImport sqlInfoImport : list) {
            if ("ODS标准层表字段英文名".equals(sqlInfoImport.getColumn())) {
                break;
            }
            id++;
            if ("s_sdt".equals(sqlInfoImport.getColumn()) || sqlInfoImport.getColumn().contains("time")  || sqlInfoImport.getColumn().contains("date")) {
                sb.append("'" + DateTimeFormatter.ofPattern(time).format(now));
            } else if ("string".equals(sqlInfoImport.getColumnType() != null ? sqlInfoImport.getColumnType().toLowerCase() : null)) {
                sb.append("'a" + id);
            } else {
                sb.append("'" + id);
            }
            sb.append("',");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append(");");
        return sb.toString();
    }
}
