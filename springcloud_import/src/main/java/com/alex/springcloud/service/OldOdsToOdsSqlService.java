package com.alex.springcloud.service;

import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.enums.AddFieldEnum;
import com.alex.springcloud.enums.CommonFieldEnum;
import com.alex.springcloud.enums.ZipperFieldEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: old ods to ods sql
 * @author:      alex
 * @createTime:  2021/2/25 9:20
 * @version:     1.0
 */
@Service
public class OldOdsToOdsSqlService {


    public String setOldOdsToOdsSql(List<SqlInfoImport> list, String odsPrefix, String odsTableName, String oldOdsPrefix,
                                     String oldOdsDatabase, String tableSysnWay) {
        String[] oldOdsData = oldOdsDatabase.split(",");
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int size = list.size();
        String table;
        sb.append(" INSERT OVERWRITE TABLE " + odsPrefix + "." + odsTableName + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
        for (int j = 0; j < oldOdsData.length; j++) {
            while(i < size) {
                sb.append("select ");
                table = null;
                for (; i < size; i++) {
                    SqlInfoImport sqlInfoImport = list.get(i);
                    if (StringUtils.isBlank(sqlInfoImport.getColumn())) {
                        continue;
                    }
                    if ("ODS标准层表字段英文名".equals(sqlInfoImport.getColumn())) {
                        break;
                    }
                    if (StringUtils.isBlank(table)) {
                        table = sqlInfoImport.getOriginSchemaTable();
                    }
                    if (sqlInfoImport.getOriginSchemaColumn() == null) {
                        switch (sqlInfoImport.getColumn()) {
                            case "s_sdt" :
                                sb.append("s_sdt,"); break;
                            case "s_org_code" :
                                sb.append((StringUtils.isBlank(oldOdsData[j]) ? "''" : oldOdsData[j].split("_")[3]) + ","); break;
                            case "s_schema" :
                                sb.append("'BIGDATA_READONLY',"); break;
                            default:
                                sb.append("'',"); break;
                        }
                    } else {
                        sb.append(sqlInfoImport.getOriginSchemaColumn() + ",");
                    }
                }
                i++;
                sb.replace(sb.length() - 1, sb.length(), "");
                sb.append(" FROM ");
                if (StringUtils.isNotBlank(oldOdsPrefix)) {
                    sb.append(oldOdsPrefix + ".");
                }
                sb.append(oldOdsData[0] + "_" + table + (SystemConstant.FULL_CN.equals(tableSysnWay) ? "_df" : "_di"));
                sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "'" );
                sb.append(" union all ");
            }
            i = 0;
        }
        sb.replace(sb.length() - 11, sb.length(), "");
        sb.append(";");
        return sb.toString();
    }
}
