package com.alex.springcloud.service;

import com.alex.springcloud.entity.GPSqlInfoImport;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/6 11:45
 * @version:     1.0
 */
@Service
public class GPSqlService {

    /**
     * @param list
     * @param greenTableName
     * @param greenTableNameCn
     * @param schema
     * @description: 创建gp创建表语句
     * @author: alex
     * @return: java.lang.String
     */
    public String setGreenplumSql(List<GPSqlInfoImport> list, String greenTableName, String greenTableNameCn, String schema) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append(" DROP TABLE IF EXISTS \"" + schema + "\".\"" + greenTableName + "\"; ");
        sb.append(" CREATE TABLE \"" + schema + "\".\"" + greenTableName + "\" ( ");
        for (GPSqlInfoImport sqlInfoImport : list) {
            switch (sqlInfoImport.getColumnType() != null ? sqlInfoImport.getColumnType().toLowerCase() : "") {
                case "bigint" :  sb.append(" \"" + sqlInfoImport.getColumn() + "\" bigint, "); break;
                case "double" :  sb.append(" \"" + sqlInfoImport.getColumn() + "\" double precision, "); break;
                default: sb.append(" \"" + sqlInfoImport.getColumn() + "\" varchar, ");
            }
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append(" ); ");
        for (GPSqlInfoImport sqlInfoImport : list) {
            sb.append(" COMMENT ON COLUMN \"" + schema + "\".\"" + greenTableName + "\".\"" + sqlInfoImport.getColumn() + "\" IS '"
                    + (sqlInfoImport.getSource() != null ? sqlInfoImport.getSource() : (sqlInfoImport.getRemark() != null ? sqlInfoImport.getRemark() : "")) + "'; ");
        }
        sb.append(" COMMENT ON TABLE \"" + schema + "\".\"" + greenTableName + "\" IS '" + greenTableNameCn + "'; ");
        return sb.toString();
    }
}
