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
 * @description: ods到dwd sql
 * @author:      alex
 * @createTime:  2020/11/23 17:00
 * @version:     1.0
 */
@Service
public class OdsToDwdSqlService {

    /**
     * @param list            列信息
     * @param odsTableName    ods表明
     * @param dwdTableNameI   dwd增量表名
     * @param dwdTableNameF   dwd全量表名
     * @description:  生成ods到dwd的初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF,
                                     String odsPrefix, String[] createTimes, String[] updateTimes, String tableType, String tableSysnWay) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.FACT_TABLE.equals(tableType))
            return setOdsToDwdInitSqlAdd(columns.toString(), list.get(0).getColumn(), noHeadColumns.toString(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes, tableSysnWay);
        else if (SystemConstant.DIMENSION_TABLE.equals(tableType) || SystemConstant.DICTIONARY_TABLE.equals(tableType))
            return setOdsToDwdInitSqlFull(noHeadColumns.toString(), list.get(0).getColumn(), odsTableName, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes);
        return "";
    }

    /**
     * @param noHeadColumns     无表头列信息（xxx,..）
     * @param odsTableName      ods表名
     * @param dwdTableNameF     dwd全量表名
     * @param sysCode           系统编号（ods表名ods_后到下一个_的数据）
     * @description:            ods到dwd维度表初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSqlFull(String noHeadColumns, String firstColumn, String odsTableName, String dwdTableNameF,
                                         String sysCode, String odsPrefix, String[] createTimes, String[] updateTimes) {
        boolean flag = (createTimes != null && createTimes.length > 0) || (updateTimes != null && updateTimes.length > 0);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_', " + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns);
        if (flag) {
            sb.append(" ,CASE ");
        }
        if (updateTimes != null && updateTimes.length > 0)
            for (String updateTime : updateTimes) {
                String newUpdateTime = dealTime(updateTime);
                sb.append(" WHEN " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
            }
        if (createTimes != null && createTimes.length > 0)
            for (String createTime : createTimes) {
                String newCreateTime = dealTime(createTime);
                sb.append(" WHEN " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
            }
        if (flag) {
            sb.append(" ELSE '19710101000000'");
        } else {
            sb.append(" ,'19710101000000'");
        }
        if (flag) {
            sb.append(" END ");
        }
        sb.append(" as " + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append(" ,'20990101000000' as " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append(" ,'1' as " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "';");
        return sb.toString();
    }

    /**
     * @param noHeadColumns    无表头列信息（xxx,..）
     * @param odsTableName     ods表名
     * @param dwdTableNameI    dwd增量事实表名
     * @param dwdTableNameF    dwd全量事实表名
     * @param sysCode          系统编号（ods表名ods_后到下一个_的数据）
     * @description:           ods到dwd事实表初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSqlAdd(String columns, String firstColumn, String noHeadColumns, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode,
                                        String odsPrefix, String[] createTimes, String[] updateTimes, String tableSysnWay) {
        StringBuilder sb = new StringBuilder();
        if (SystemConstant.ADD_CN.equals(tableSysnWay)) {
            boolean flag = (updateTimes != null && updateTimes.length > 0) || (createTimes != null && createTimes.length > 0);
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT ");
            sb.append(" record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            for(Map.Entry entry : SystemConstant.DWD_ADD_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            for (Map.Entry entry : SystemConstant.DWD_COMMON_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            sb.append(" FROM (");
            sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_', " + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,'insert' as " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_', s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM ");
            if (StringUtils.isNotBlank(odsPrefix))
                sb.append(odsPrefix + ".");
            sb.append(odsTableName);
            sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "') record;");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF +" PARTITION(ds='" + SystemConstant.YESTERDAY + "')");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            if (flag) {
                sb.append(" ,CASE ");
            }
            if (updateTimes != null && updateTimes.length > 0)
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
                }
            if (createTimes != null && createTimes.length > 0)
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append(" WHEN " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
                }
            if (flag) {
                sb.append(" ELSE '19710101000000'");
            } else {
                sb.append(" ,'19710101000000'");
            }
            if (flag) {
                sb.append(" END ");
            }
            sb.append(" as " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' as " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(",'1' as " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(",CONCAT('" + sysCode + "_', s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM " + dwdTableNameI);
            sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "';");
        } else if (SystemConstant.FULL_CN.equals(tableSysnWay)){
            sb.append(" --INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT ");
            sb.append(" record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            for(Map.Entry entry : SystemConstant.DWD_ADD_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            for (Map.Entry entry : SystemConstant.DWD_COMMON_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            sb.append(" FROM (");
            sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,'insert' as " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_', s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM ");
            if (StringUtils.isNotBlank(odsPrefix))
                sb.append(odsPrefix + ".");
            sb.append(odsTableName);
            sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "') record;");
            sb.append("\n");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF +" PARTITION(ds='" + SystemConstant.YESTERDAY + "')");
            sb.append(" SELECT ");
            sb.append(" record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            for(Map.Entry entry : SystemConstant.DWD_ZIPPER_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            for (Map.Entry entry : SystemConstant.DWD_COMMON_TABLE.entrySet())
                sb.append(",record." + entry.getKey());
            sb.append(" FROM (");
            sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,'19710101000000'");
            sb.append(" as " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' as " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(",'1' as " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(",CONCAT('" + sysCode + "_', s_org_code) as  " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM ");
            if (StringUtils.isNotBlank(odsPrefix))
                sb.append(odsPrefix + ".");
            sb.append(odsTableName);
            sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "') record;");
        }
        return sb.toString();
    }

    private String dealTime(String time) {
        if (StringUtils.isEmpty(time) || !time.contains("+"))
            return time;
        String[] split = time.split("\\+");
        StringBuilder sb = new StringBuilder();
        sb.append("concat(");
        for(String sp : split)
            sb.append(sp + ",");
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append(")");
        return sb.toString();
    }

    /**
     * @param list            列信息
     * @param odsTableName    ods表明
     * @param dwdTableNameI   dwd增量表名
     * @param dwdTableNameF   dwd全量表名
     * @param odsPrefix       ods标准所在空间
     * @description:          生成ods到dwd的同步sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF,
                                 String odsPrefix, String[] createTimes, String[] updateTimes, String tableType, String tableSysnWay) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.FACT_TABLE.equals(tableType))
            return setOdsToDwdSqlAdd(columns.toString(), noHeadColumns.toString(), list.get(0).getColumn(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes, tableSysnWay);
        else if (SystemConstant.DIMENSION_TABLE.equals(tableType) || SystemConstant.DICTIONARY_TABLE.equals(tableType))
            return setOdsToDwdSqlFull(columns.toString(), noHeadColumns.toString(), list.get(0).getColumn(), odsTableName, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes);
        return "";
    }

    /**
     * @param columns         有表头列信息（record.xxx,..）
     * @param noHeadColumns   ods表名
     * @param odsTableName    dwd增量事实表名
     * @param dwdTableNameF   dwd全量事实表名
     * @param sysCode         系统编号（ods表名ods_后到下一个_的数据）
     * @param odsPrefix       ods标准所在空间
     * @description:          生成ods到dwd维度表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlFull(String columns, String noHeadColumns, String firstColumn, String odsTableName, String dwdTableNameF,
                                     String sysCode, String odsPrefix, String[] createTimes, String[] updateTimes) {
        StringBuilder sb = new StringBuilder();
        sb.append(" WITH at_" + odsTableName + " AS ( ");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode() + ",");
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append(",cnt, max_s_sdt, max_ds, row_number() over (partition by " + firstColumn + ",s_org_code order by max_ds desc) as rm FROM ");
        sb.append(" ( ");
        sb.append("         SELECT ");
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append("        ,count(1) as cnt,MAX(s_sdt) as max_s_sdt,MAX(ds) as max_ds ");
        sb.append("        FROM ");
        sb.append("                ( ");
        sb.append("                        SELECT * FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.TWO_DAYS_AGO + "'");
        sb.append("                        UNION ALL ");
        sb.append("                        SELECT * FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.YESTERDAY + "' ");
        sb.append("                 ) a ");
        sb.append("         GROUP BY " + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append(" ) b ");
        sb.append(" WHERE cnt=1 ");
        sb.append(" ), ");
        sb.append(" tb_update_" + odsTableName + " AS ( SELECT " + CommonFieldEnum.S_KEY);
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
        sb.append("   ,cnt, max_s_sdt, max_ds, rm, cnt2, " + AddFieldEnum.S_ACTION.getCode() + " FROM  ( SELECT ");
        sb.append("                 at.* ");
        sb.append("                ,atc.cnt as cnt2 ");
        sb.append("                 ,CASE WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
        sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
        sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.YESTERDAY + "' THEN 'insert' ");
        sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.TWO_DAYS_AGO + "' THEN 'delete' ");
        sb.append(" END AS " + AddFieldEnum.S_ACTION.getCode());
        sb.append(" FROM at_" + odsTableName + " at LEFT JOIN ( SELECT " + CommonFieldEnum.S_KEY.getCode() + ",COUNT(1) as cnt FROM at_" + odsTableName + " GROUP BY " + CommonFieldEnum.S_KEY.getCode() + " ) atc ON at." + CommonFieldEnum.S_KEY.getCode() + "=atc." + CommonFieldEnum.S_KEY.getCode() + " ) tb_u ");
        sb.append(" WHERE " + AddFieldEnum.S_ACTION.getCode() + " <> 'his' AND " + AddFieldEnum.S_ACTION.getCode() + " IS NOT NULL) ");
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
        sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
        sb.append(columns);
        sb.append("         ,record." + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append("        ,CASE ");
        if (updateTimes != null && updateTimes.length > 0) {
            for (String updateTime : updateTimes) {
                String newUpdateTime = dealTime(updateTime);
                sb.append(" WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL and " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
            }
        }
        sb.append(" WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL AND " + AddFieldEnum.S_ACTION.getCode() + " =  'delete' THEN TO_CHAR(DATEADD(TO_DATE(max_s_sdt,'yyyymmddhhmiss'), 1, 'dd') ,'yyyymmddhhmiss') ");
        sb.append(" WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL THEN tb_update.max_s_sdt ");
        sb.append(" ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append("        ,CASE    WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL THEN '0' ");
        sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
        sb.append("        ,record." + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM    ( ");
        sb.append("         SELECT  *,row_number() over (partition by " + CommonFieldEnum.S_KEY.getCode() + " order by " + ZipperFieldEnum.S_END_TIME.getCode() + " desc) as rm ");
        sb.append("         FROM " + dwdTableNameF);
        sb.append("        WHERE   ds = '" + SystemConstant.TWO_DAYS_AGO + "'");
        sb.append(" ) record LEFT ");
        sb.append(" JOIN  tb_update_" + odsTableName + "  tb_update ");
        sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode());
        sb.append("  UNION ");
        sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt as s_sdt").replace(",S_SDT", ",max_s_sdt as s_sdt"));
        sb.append(",CASE ");
        if (createTimes != null && createTimes.length > 0) {
            for (String createTime : createTimes) {
                String newCreateTime = dealTime(createTime);
                sb.append("  WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' AND " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
            }
        }
        if (updateTimes != null && updateTimes.length > 0) {
            for (String updateTime : updateTimes) {
                String newUpdateTime = dealTime(updateTime);
                sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' and " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
            }
        }
        sb.append("  WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' THEN '19710101000000' ");
        if (updateTimes != null && updateTimes.length > 0) {
            for (String updateTime : updateTimes) {
                String newUpdateTime = dealTime(updateTime);
                sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'update' and " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
            }
        }
        sb.append("  ELSE max_s_sdt ");
        sb.append(" END as " + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append("         ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append("        ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM    tb_update_" + odsTableName);
        sb.append(" WHERE " + AddFieldEnum.S_ACTION.getCode() + " = 'update' ");
        sb.append(" OR " + AddFieldEnum.S_ACTION.getCode() + " = 'insert'; ");
        return sb.toString();
    }

    /**
     * @param columns          有表头列信息（record.xxx,..）
     * @param noHeadColumns    无表头列信息（xxx,..）
     * @param odsTableName     ods表名
     * @param dwdTableNameI    dwd增量事实表名
     * @param dwdTableNameF    dwd全量事实表名
     * @param sysCode          系统编号（ods表名ods_后到下一个_的数据）
     * @param odsPrefix        ods标准所在空间
     * @description:           生成ods到dwd事实表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlAdd(String columns, String noHeadColumns, String firstColumn, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode,
                                    String odsPrefix, String[] createTimes, String[] updateTimes, String tableSysnWay) {
        StringBuilder sb = new StringBuilder();
        if (SystemConstant.FULL_CN.equals(tableSysnWay)) {
            boolean flag = (createTimes != null && createTimes.length > 0) || (updateTimes != null && updateTimes.length > 0);
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT  * ");
            sb.append(" ,ROW_NUMBER() OVER (PARTITION BY " + firstColumn + ",s_org_code ORDER BY max_ds DESC) AS rm ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", "").replace("S_SDT", ""));
            sb.append(" ,COUNT(1) AS cnt ");
            sb.append(" ,MAX(s_sdt) AS max_s_sdt ");
            sb.append(" ,MAX(ds) AS max_ds ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,ds ");
            sb.append(" FROM    " + odsPrefix + "." + odsTableName );
            sb.append(" WHERE   ds = '" + SystemConstant.TWO_DAYS_AGO + "'");
            sb.append(" UNION ALL ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,ds ");
            sb.append(" FROM    " + odsPrefix +  "." + odsTableName);
            sb.append(" WHERE   ds = '" + SystemConstant.YESTERDAY + "' ");
            sb.append(" ) a ");
            sb.append(" GROUP BY " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
            sb.append(" ) b ");
            sb.append(" WHERE   cnt = 1; ");

            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS ");
            sb.append(" SELECT  * ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT  tmp_at.* ");
            sb.append(" ,atc.cnt AS cnt2 ");
            sb.append(" ,CASE    WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
            sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.YESTERDAY + "' THEN 'insert' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.TWO_DAYS_AGO + "' THEN 'delete' ");
            sb.append(" END AS " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" FROM    tmp_at_" + odsTableName + " tmp_at LEFT ");
            sb.append(" JOIN    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ,COUNT(1) AS cnt ");
            sb.append(" FROM    tmp_at_" + odsTableName);
            sb.append(" GROUP BY " + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ) atc ");
            sb.append(" ON  tmp_at." + CommonFieldEnum.S_KEY.getCode() + " = atc." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ) tb_u ");
            sb.append("  WHERE   " + AddFieldEnum.S_ACTION.getCode() + " <> 'his' ");
            sb.append(" AND     " + AddFieldEnum.S_ACTION.getCode() + " IS NOT NULL ");
            sb.append(" ; \n");
            if (!flag)
                sb.append("--");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append("  ,record." + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt AS s_sdt").replace("S_SDT", ",max_s_sdt AS s_sdt"));
            sb.append(" ," + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName);
            sb.append(" ) record ");
            sb.append(" ; \n");

            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF);
            sb.append(" PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append(" ,record." + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,CASE ");
            if (flag && updateTimes != null && updateTimes.length > 0) {
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN record.rm=1 AND tb_update." + firstColumn + " IS NOT NULL AND " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
                }
            }
            sb.append(" WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL AND " + AddFieldEnum.S_ACTION.getCode() + " =  'delete' THEN TO_CHAR(DATEADD(TO_DATE(max_s_sdt,'yyyymmddhhmiss'), 1, 'dd') ,'yyyymmddhhmiss') ");
            sb.append(" WHEN record.rm=1 AND tb_update." + firstColumn + " IS NOT NULL THEN tb_update.max_s_sdt ");
            sb.append(" ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,CASE    WHEN record.rm=1 AND tb_update." + firstColumn + " IS NOT NULL THEN '0' ");
            sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append(" SELECT  * ");
            sb.append(" ,ROW_NUMBER() OVER (PARTITION BY " + CommonFieldEnum.S_KEY.getCode() + " ORDER BY " + ZipperFieldEnum.S_END_TIME.getCode() + " DESC) AS rm ");
            sb.append(" FROM " + dwdTableNameF);
            sb.append(" WHERE   ds = '" + SystemConstant.TWO_DAYS_AGO + "' ");
            sb.append(" ) record LEFT ");
            sb.append(" JOIN    tb_update_" + odsTableName + " tb_update ");
            sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" UNION ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", ", max_s_sdt AS s_sdt ").replace(",S_SDT", ",max_s_sdt AS s_sdt "));
            sb.append(" ,CASE ");
            if (flag && createTimes != null && createTimes.length > 0) {
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' AND " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
                }
            }
            sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' THEN '19710101000000' ");
            sb.append(" ELSE max_s_sdt ");
            sb.append(" END AS " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName);
            sb.append(" WHERE " + AddFieldEnum.S_ACTION.getCode() + " = 'update' ");
            sb.append(" OR " + AddFieldEnum.S_ACTION.getCode() + " = 'insert'; ");
            sb.append(" DROP TABLE IF EXISTS tmp_at_"+ odsTableName + " ; ");
            sb.append(" DROP TABLE IF EXISTS tb_update_" + odsTableName + " ; ");
        } else if (SystemConstant.ADD_CN.equals(tableSysnWay)){
            boolean flag = (createTimes != null && createTimes.length > 0) || (updateTimes != null && updateTimes.length > 0);
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT  * ");
            sb.append(" ,ROW_NUMBER() OVER (PARTITION BY " + firstColumn + ",s_org_code ORDER BY max_ds DESC) AS rm ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", ",MAX(s_sdt) AS max_s_sdt"));
            sb.append(" ,COUNT(1) AS cnt ");
            sb.append(" ,MAX(ds) AS max_ds ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,ds ");
            sb.append(" FROM " + dwdTableNameF);
            sb.append(" WHERE   ds = ' " + SystemConstant.TWO_DAYS_AGO + "' ");
            sb.append(" AND     s_stat = '1' ");
            sb.append(" UNION ALL ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_',s_org_code,'_'," + firstColumn + ") AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,ds ");
            sb.append(" FROM    " + odsPrefix + "." + odsTableName);
            sb.append(" WHERE   ds = '" + SystemConstant.YESTERDAY + "' ");
            sb.append(" ) a ");
            sb.append(" GROUP BY " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", ""));
            sb.append(" ) b ");
            sb.append(" WHERE   cnt = 1; ");
            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS ");
            sb.append(" SELECT  * ");
            sb.append(" FROM    ( ");
            sb.append(" SELECT  tmp_at.* ");
            sb.append(" ,atc.cnt AS cnt2 ");
            sb.append(" ,CASE    WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
            sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.YESTERDAY + "' THEN 'insert'");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.TWO_DAYS_AGO + "' THEN 'delete'");
            sb.append(" END AS " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" FROM    tmp_at_" + odsTableName + " tmp_at LEFT");
            sb.append(" JOIN    (");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ,COUNT(1) AS cnt");
            sb.append(" FROM    tmp_at_" + odsTableName);
            sb.append(" GROUP BY " + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ) atc ");
            sb.append(" ON      tmp_at." + CommonFieldEnum.S_KEY.getCode() + " = atc." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" ) tb_u ");
            sb.append(" WHERE   " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' ");
            sb.append(" OR      " + AddFieldEnum.S_ACTION.getCode() + " = 'update'; ");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append(" ,record." + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt AS s_sdt").replace("S_SDT", ",max_s_sdt AS s_sdt"));
            sb.append(" ," + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName);
            sb.append(" ) record; ");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append(" ,record." + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,CASE ");
            if (flag && updateTimes != null && updateTimes.length > 0) {
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN record.rm=1 AND tb_update." + firstColumn + " IS NOT NULL AND record." + newUpdateTime + " IS NOT NULL THEN TO_CHAR( TO_DATE(record." + newUpdateTime + ",'yyyy-mm-dd hh:mi:ss') ,'yyyymmddhhmiss' ) ");
                }
            }
            sb.append(" WHEN record.rm=1 AND tb_update.id IS NOT NULL THEN tb_update.max_s_sdt ");
            sb.append(" ELSE record. " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,CASE    WHEN record.rm=1 AND tb_update.id IS NOT NULL THEN '0' ");
            sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" END AS  " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append(" SELECT  * ");
            sb.append(" ,ROW_NUMBER() OVER (PARTITION BY " + CommonFieldEnum.S_KEY.getCode() + " ORDER BY " + ZipperFieldEnum.S_END_TIME.getCode() + " DESC) AS rm ");
            sb.append(" FROM  " + dwdTableNameF);
            sb.append(" WHERE   ds = '" + SystemConstant.TWO_DAYS_AGO + "' ");
            sb.append(" ) record LEFT ");
            sb.append(" JOIN    tb_update_" + odsTableName +  " tb_update ");
            sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode() + " ");
            sb.append(" UNION ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,CASE ");
            if (flag && createTimes != null && createTimes.length > 0) {
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' AND " + newCreateTime + " IS NOT NULL THEN TO_CHAR( TO_DATE( " + newCreateTime + " ,'yyyy-mm-dd hh:mi:ss') ,'yyyymmddhhmiss' ) ");
                }
            }
            sb.append(" WHEN " + AddFieldEnum.S_ACTION.getCode() + " = 'insert' THEN '19710101000000' ");
            sb.append(" ELSE s_sdt ");
            sb.append(" END AS " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" , " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM " + dwdTableNameI);
            sb.append(" WHERE   ds = '" + SystemConstant.YESTERDAY + "';");
            sb.append(" DROP TABLE IF EXISTS tmp_at_" + odsTableName + " ;");
            sb.append(" DROP TABLE IF EXISTS tb_update_" + odsTableName + " ;");


//
//            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
//            sb.append(" SELECT *,row_number() over (partition by " + firstColumn + " order by max_ds desc) as rm FROM ");
//            sb.append("         ( ");
//            sb.append("                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
//            sb.append("                 ,count(1) as cnt,MAX(s_sdt) as max_s_sdt,MAX(ds) as max_ds ");
//            sb.append("                FROM ");
//            sb.append("                         ( ");
//            sb.append("                                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
//            sb.append("                                ,ds ");
//            sb.append("                                 FROM " + dwdTableNameF + " WHERE ds='" + SystemConstant.TWO_DAYS_AGO + "'");
//            sb.append("                                 UNION ALL ");
//            sb.append("                                SELECT CONCAT('" + sysCode + "_',s_org_code,'_', " + firstColumn + ") as " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
//            sb.append("                                 ,ds ");
//            sb.append(" FROM ");
//            if (StringUtils.isNotBlank(odsPrefix))
//                sb.append(odsPrefix + ".");
//            sb.append(odsTableName + " WHERE ds='" + SystemConstant.YESTERDAY + "' ");
//            sb.append("                         ) a ");
//            sb.append("                 GROUP BY " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
//            sb.append("         ) b ");
//            sb.append(" WHERE cnt=1; ");
//            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS SELECT * FROM ");
//            sb.append("         ( SELECT ");
//            sb.append("                 tmp_at.* ");
//            sb.append("                 ,atc.cnt as cnt2 ");
//            sb.append("                ,CASE WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
//            sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
//            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.YESTERDAY + "' THEN 'insert' ");
//            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.TWO_DAYS_AGO + "' THEN 'delete' ");
//            sb.append(" END AS action ");
//            sb.append(" FROM tmp_at_" + odsTableName + " tmp_at LEFT JOIN ( SELECT " + CommonFieldEnum.S_KEY.getCode() + ",COUNT(1) as cnt FROM tmp_at_" + odsTableName + " GROUP BY " + CommonFieldEnum.S_KEY.getCode() + " ) atc ON tmp_at." + CommonFieldEnum.S_KEY.getCode() + "=atc." + CommonFieldEnum.S_KEY.getCode() + " ) tb_u ");
//            sb.append(" WHERE action <> 'his'; ");
//            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION (ds='" + SystemConstant.YESTERDAY + "') ");
//            sb.append(" SELECT ");
//            sb.append("        record." + CommonFieldEnum.S_KEY.getCode());
//            sb.append(columns);
//            sb.append("        ,   record." + CommonFieldEnum.S_SRC.getCode());
//            sb.append("        ,   record." + AddFieldEnum.S_ACTION.getCode());
//            sb.append(" FROM ");
//            sb.append("         ( ");
//            sb.append("                 SELECT " + CommonFieldEnum.S_KEY.getCode());
//            sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt as s_sdt").replace(",S_SDT", ",max_s_sdt as s_sdt"));
//            sb.append("                 ,   CONCAT('" + sysCode + "_',s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
//            sb.append("                 ,   action as " + AddFieldEnum.S_ACTION.getCode());
//            sb.append("                FROM tb_update_" + odsTableName + ") ");
//            sb.append(" record; ");
//            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.YESTERDAY + "') ");
//            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
//            sb.append(columns);
//            sb.append("        ,record." + ZipperFieldEnum.S_START_TIME.getCode());
//            sb.append("         ,CASE    WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL THEN tb_update.max_s_sdt ");
//            sb.append(" ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
//            sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
//            sb.append("         ,CASE    WHEN record.rm=1 and tb_update." + firstColumn + " IS NOT NULL THEN '0' ");
//            sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
//            sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
//            sb.append("         ,record." + CommonFieldEnum.S_SRC.getCode());
//            sb.append(" FROM    ( ");
//            sb.append("         SELECT  *,row_number() over (partition by " + CommonFieldEnum.S_KEY.getCode() + " order by " + ZipperFieldEnum.S_END_TIME.getCode() + " desc) as rm ");
//            sb.append("        FROM " + dwdTableNameF);
//            sb.append("         WHERE   ds = '" + SystemConstant.TWO_DAYS_AGO + "' ");
//            sb.append(" ) record LEFT ");
//            sb.append(" JOIN    tb_update_" + odsTableName + " tb_update");
//            sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode());
//            sb.append(" UNION ");
//            sb.append(" SELECT  di." + CommonFieldEnum.S_KEY.getCode());
//            sb.append(columns.replace(",record.s_sdt", ",record.max_s_sdt as s_sdt ").replace(",record.S_SDT", ",record.max_s_sdt as s_sdt ").replace("record.", "di."));
//            sb.append(",CASE ");
//            if (updateTimes != null && updateTimes.length > 0) {
//                for (String updateTime : updateTimes) {
//                    String newUpdateTime = dealTime(updateTime);
//                    sb.append(" WHEN tb_update.action = 'insert' and tb_update." + newUpdateTime + " IS NOT NULL THEN tb_update." + newUpdateTime);
//                }
//            }
//            if (createTimes != null && createTimes.length > 0) {
//                for (String createTime : createTimes) {
//                    String newCreateTime = dealTime(createTime);
//                    sb.append("  WHEN tb_update.action = 'insert' AND tb_update." + newCreateTime + " IS NOT NULL THEN tb_update." + newCreateTime);
//                }
//            }
//            sb.append("  WHEN tb_update.action = 'insert' THEN '19710101000000' ");
//            sb.append(" ELSE tb_update.max_s_sdt ");
//            sb.append(" END as " + ZipperFieldEnum.S_START_TIME.getCode());
//            sb.append("        ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
//            sb.append("        ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
//            sb.append("        ,di." + CommonFieldEnum.S_SRC.getCode());
//            sb.append(" FROM  tb_update_" + odsTableName + "   tb_update ");
//            sb.append(" LEFT JOIN (SELECT * FROM " + dwdTableNameI +" WHERE ds='" + SystemConstant.YESTERDAY + "') di ");
//            sb.append(" ON tb_update." + CommonFieldEnum.S_KEY.getCode() + "=di." + CommonFieldEnum.S_KEY.getCode());
//            sb.append(" WHERE   tb_update.action = 'update' ");
//            sb.append(" OR      tb_update.action = 'insert'; ");
//            sb.append(" DROP TABLE tmp_at_" + odsTableName + "; ");
//            sb.append(" DROP TABLE tb_update_" + odsTableName + "; ");
        }
        return sb.toString();
    }
}
