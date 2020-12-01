package com.alex.springcloud.service;

import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.enums.AddFieldEnum;
import com.alex.springcloud.enums.CommonFieldEnum;
import com.alex.springcloud.enums.ZipperFieldEnum;
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
     * @param type            传入类型（全量、增量）
     * @description:  生成ods到dwd的初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF, String type) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.ADD_TABLE.equals(type))
            return setOdsToDwdInitSqlAdd(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode);
        else if (SystemConstant.FULL_TABLE.equals(type))
            return setOdsToDwdInitSqlFull(noHeadColumns.toString(), odsTableName, dwdTableNameF, sysCode);
        return "";
    }

    /**
     * @param noHeadColumns     无表头列信息（id,..）
     * @param odsTableName      ods表名
     * @param dwdTableNameF     dwd全量表名
     * @param sysCode           系统编号（ods表名ods_后到下一个_的数据）
     * @description:            ods到dwd维度表初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSqlFull(String noHeadColumns, String odsTableName, String dwdTableNameF, String sysCode) {
        boolean flag = noHeadColumns.contains(SystemConstant.CREATETIME) || noHeadColumns.contains(SystemConstant.UPDATETIME);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns);
        if (flag) {
            sb.append(" ,CASE ");
        }
        if (noHeadColumns.contains(SystemConstant.UPDATETIME)) {
            sb.append(" WHEN update_time IS NOT NULL THEN TO_CHAR(TO_DATE(update_time,'yyyy-mm-dd hh:mi:ss'),'yyyymmddhhmiss') ");
        }
        if (noHeadColumns.contains(SystemConstant.CREATETIME)) {
            sb.append(" WHEN " + SystemConstant.CREATETIME + " IS NOT NULL THEN TO_CHAR(TO_DATE(" + SystemConstant.CREATETIME + ",'yyyy-mm-dd hh:mi:ss'),'yyyymmddhhmiss') ");
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
        sb.append(" ,CONCAT(' " + sysCode + "_',s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
        sb.append("  FROM " + odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "';");
        return sb.toString();
    }

    /**
     * @param noHeadColumns    无表头列信息（id,..）
     * @param odsTableName     ods表名
     * @param dwdTableNameI    dwd增量事实表名
     * @param dwdTableNameF    dwd全量事实表名
     * @param sysCode          系统编号（ods表名ods_后到下一个_的数据）
     * @description:           ods到dwd事实表初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSqlAdd(String columns, String noHeadColumns, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode) {
        boolean flag = noHeadColumns.contains(SystemConstant.CREATETIME) || noHeadColumns.contains(SystemConstant.UPDATETIME);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
        sb.append(" SELECT ");
        sb.append(" record." + CommonFieldEnum.S_KEY.getCode());
        sb.append(columns);
        for(Map.Entry entry : SystemConstant.DWD_ADD_TABLE.entrySet())
            sb.append(",record." + entry.getKey());
        for (Map.Entry entry : SystemConstant.DWD_COMMON_TABLE.entrySet())
            sb.append(",record." + entry.getKey());
        sb.append(" FROM (");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns);
        sb.append(" ,'insert' as " + AddFieldEnum.S_ACTION.getCode());
        sb.append(" ,CONCAT('" + sysCode + "_', s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM " + odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "') record;");
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF +" PARTITION(ds='" + SystemConstant.CURTIME + "')");
        sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns);
        if (flag) {
            sb.append(" ,CASE ");
        }
        if (noHeadColumns.contains(SystemConstant.UPDATETIME)) {
            sb.append(" WHEN " + SystemConstant.UPDATETIME + " IS NOT NULL THEN " + SystemConstant.UPDATETIME);
        }
        if (noHeadColumns.contains(SystemConstant.CREATETIME)) {
            sb.append(" WHEN " + SystemConstant.CREATETIME + " IS NOT NULL THEN " + SystemConstant.CREATETIME);
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
        sb.append("," + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM " + dwdTableNameI);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "';");



        return sb.toString();
    }

    /**
     * @param list            列信息
     * @param odsTableName    ods表明
     * @param dwdTableNameI   dwd增量表名
     * @param dwdTableNameF   dwd全量表名
     * @param type            传入类型（全量、增量）
     * @description:          生成ods到dwd的同步sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF, String type, Integer isZipper) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.ADD_TABLE.equals(type))
            return setOdsToDwdSqlAdd(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode, isZipper);
        else if (SystemConstant.FULL_TABLE.equals(type))
            return setOdsToDwdSqlFull(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameF, sysCode);
        return "";
    }

    /**
     * @param columns         有表头列信息（record.id,..）
     * @param noHeadColumns   ods表名
     * @param odsTableName    dwd增量事实表名
     * @param dwdTableNameF   dwd全量事实表名
     * @param sysCode         系统编号（ods表名ods_后到下一个_的数据）
     * @description:          生成ods到dwd维度表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlFull(String columns, String noHeadColumns, String odsTableName, String dwdTableNameF, String sysCode) {
        boolean flag = noHeadColumns.contains(SystemConstant.CREATETIME) || noHeadColumns.contains(SystemConstant.UPDATETIME);
        StringBuilder sb = new StringBuilder();
        sb.append(" WITH at_" + odsTableName + " AS ( ");
        sb.append(" SELECT CONCAT(' " + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode() + ",");
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append(",cnt, max_s_sdt, max_ds, row_number() over (partition by id order by max_ds desc) as rm FROM ");
        sb.append(" ( ");
        sb.append("         SELECT ");
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append("        ,count(1) as cnt,MAX(s_sdt) as max_s_sdt,MAX(ds) as max_ds ");
        sb.append("        FROM ");
        sb.append("                ( ");
        sb.append("                        SELECT * FROM " + odsTableName + " WHERE ds='" + SystemConstant.CURTIME + "' - 1 ");
        sb.append("                        UNION ALL ");
        sb.append("                        SELECT * FROM " + odsTableName + " WHERE ds='" + SystemConstant.CURTIME + "' ");
        sb.append("                 ) a ");
        sb.append("         GROUP BY " + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append(" ) b ");
        sb.append(" WHERE cnt=1 ");
        sb.append(" ), ");
        sb.append(" tb_update_" + odsTableName + " AS ( SELECT " + CommonFieldEnum.S_KEY);
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
        sb.append("   ,cnt, max_s_sdt, max_ds, rm, cnt2, action FROM  ( SELECT ");
        sb.append("                 at.* ");
        sb.append("                ,atc.cnt as cnt2 ");
        sb.append("                 ,CASE WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
        sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
        sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + "' THEN 'insert' ");
        sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + "' - 1 THEN 'delete' ");
        sb.append(" END AS action ");
        sb.append(" FROM at_" + odsTableName + " at LEFT JOIN ( SELECT " + CommonFieldEnum.S_KEY.getCode() + ",COUNT(1) as cnt FROM at_" + odsTableName + " GROUP BY " + CommonFieldEnum.S_KEY.getCode() + " ) atc ON at." + CommonFieldEnum.S_KEY.getCode() + "=atc." + CommonFieldEnum.S_KEY.getCode() + " ) tb_u ");
        sb.append(" WHERE action <> 'his') ");
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
        sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
        sb.append(columns);
        sb.append("         ,record." + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append("        ,CASE    WHEN record.rm=1 and tb_update.id IS NOT NULL THEN tb_update.max_s_sdt ");
        sb.append(" ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append("        ,CASE    WHEN record.rm=1 and tb_update.id IS NOT NULL THEN '0' ");
        sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
        sb.append("        ,record." + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM    ( ");
        sb.append("         SELECT  *,row_number() over (partition by " + CommonFieldEnum.S_KEY.getCode() + " order by " + ZipperFieldEnum.S_END_TIME.getCode() + " desc) as rm ");
        sb.append("         FROM " + dwdTableNameF);
        sb.append("        WHERE   ds = '" + SystemConstant.CURTIME + "' - 1");
        sb.append(" ) record LEFT ");
        sb.append(" JOIN  tb_update_" + odsTableName + "  tb_update ");
        sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode());
        sb.append("  UNION ");
        sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt as s_sdt").replace(",S_SDT", ",max_s_sdt as s_sdt"));
        sb.append(",CASE ");
        if (noHeadColumns.contains(SystemConstant.UPDATETIME)) {
            sb.append(" WHEN action = 'insert' and " + SystemConstant.UPDATETIME + " IS NOT NULL THEN " + SystemConstant.UPDATETIME);
        }
        if (noHeadColumns.contains(SystemConstant.CREATETIME)) {
            sb.append("  WHEN action = 'insert' AND " + SystemConstant.CREATETIME + " IS NOT NULL THEN " + SystemConstant.CREATETIME);
        }
        sb.append("  WHEN tb_update.action = 'insert' THEN '19710101000000' ");
        sb.append("  ELSE tb_update.max_s_sdt ");
        sb.append(" END as " + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append("         ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append("        ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM    tb_update_" + odsTableName);
        sb.append(" WHERE   " + AddFieldEnum.S_ACTION.getCode() + " = 'update' ");
        sb.append(" OR      " + AddFieldEnum.S_ACTION.getCode() + " = 'insert'; ");
        return sb.toString();
    }

    /**
     * @param columns          有表头列信息（record.id,..）
     * @param noHeadColumns    无表头列信息（id,..）
     * @param odsTableName     ods表名
     * @param dwdTableNameI    dwd增量事实表名
     * @param dwdTableNameF    dwd全量事实表名
     * @param sysCode          系统编号（ods表名ods_后到下一个_的数据）
     * @description:           生成ods到dwd事实表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlAdd(String columns, String noHeadColumns, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode, Integer isZipper) {
        boolean flag = noHeadColumns.contains(SystemConstant.CREATETIME) || noHeadColumns.contains(SystemConstant.UPDATETIME);
        StringBuilder sb = new StringBuilder();
        if (isZipper == 0) {
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_',s_org_code,'_',id) AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,'insert' AS " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" FROM " + odsTableName);
            sb.append(" WHERE ds = '" + SystemConstant.CURTIME + "';");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ," + AddFieldEnum.S_ACTION.getCode());
            sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM tmp_at_" + odsTableName + ";");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF);
            sb.append(" PARTITION(ds='" + SystemConstant.CURTIME + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append(" ,record." + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,record." + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,record." + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    " + dwdTableNameF + " record ");
            sb.append(" WHERE   ds = '" + SystemConstant.CURTIME + "' - 1 ");
            sb.append("  UNION ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            if (flag) {
                sb.append(" ,CASE ");
            }
            if (noHeadColumns.contains(SystemConstant.UPDATETIME)) {
                sb.append(" WHEN " + SystemConstant.UPDATETIME + " IS NOT NULL THEN " + SystemConstant.UPDATETIME);
            }
            if (noHeadColumns.contains(SystemConstant.CREATETIME)) {
                sb.append(" WHEN " + SystemConstant.CREATETIME + " IS NOT NULL THEN " + SystemConstant.CREATETIME);
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
            sb.append(" ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,CONCAT('1020005_',s_org_code) AS s_src ");
            sb.append(" FROM    tmp_at_" + odsTableName + ";");
            sb.append(" DROP TABLE tmp_at_" + odsTableName + " ; ");
        } else if (noHeadColumns.contains(SystemConstant.CREATETIME) && noHeadColumns.contains(SystemConstant.UPDATETIME) && noHeadColumns.contains(SystemConstant.ISVALID)) {
            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_', record.s_org_code, '_', record.id) AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append("         ,CASE    WHEN " + SystemConstant.CREATETIME + " IS NOT NULL AND " + SystemConstant.CREATETIME + " >= '" + SystemConstant.CURTIME + "' THEN 'insert' ");
            sb.append(" WHEN " + SystemConstant.UPDATETIME + " IS NOT NULL AND " + SystemConstant.UPDATETIME + " >= '" + SystemConstant.CURTIME + "' THEN 'update' ");
            sb.append(" END AS " + AddFieldEnum.S_ACTION.getCode());
            sb.append("         ,CONCAT('" + sysCode + "_', record.s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    " + odsTableName + " AS record ");
            sb.append(" WHERE   ds = '" + SystemConstant.CURTIME + "';");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION(ds='" + SystemConstant.CURTIME +"') ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(", " + AddFieldEnum.S_ACTION.getCode());
            sb.append(", " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName + ";");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
            sb.append(" SELECT   record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append("         ,record.s_start_time " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append("         ,CASE    WHEN b.id IS NOT NULL AND rm = 1 THEN b.update_time ");
            sb.append("         ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append("         ,CASE    WHEN b.id IS NOT NULL AND rm = 1 THEN '0' ");
            sb.append(" ELSE record." + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append("         ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append("         SELECT  * ");
            sb.append("         ,ROW_NUMBER() OVER (PARTITION BY id ORDER BY s_end_time DESC) AS rm ");
            sb.append("         FROM " + dwdTableNameF);
            sb.append("         WHERE   ds = '" + SystemConstant.CURTIME + "' - 1");
            sb.append(" ) record LEFT ");
            sb.append(" JOIN    tb_update_" + odsTableName + " b ");
            sb.append(" ON      record.id = b.id ");
            sb.append(" UNION ");
            sb.append(" SELECT  s_key " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,CASE WHEN " + SystemConstant.UPDATETIME + " IS NOT NULL THEN " + SystemConstant.UPDATETIME);
            sb.append("  WHEN " + SystemConstant.CREATETIME + " IS NOT NULL THEN " + SystemConstant.CREATETIME + " ELSE '19710101000000' END AS " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ," + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName);
            sb.append(" WHERE   " + AddFieldEnum.S_ACTION.getCode() + " = 'update' ");
            sb.append(" OR      " + AddFieldEnum.S_ACTION.getCode() +" = 'insert' ");
            sb.append("         ; ");
            sb.append(" DROP TABLE IF EXISTS tb_update_" + odsTableName + ";");
        } else  {
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT *,row_number() over (partition by id order by max_ds desc) as rm FROM ");
            sb.append("         ( ");
            sb.append("                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("                 ,count(1) as cnt,MAX(s_sdt) as max_s_sdt,MAX(ds) as max_ds ");
            sb.append("                FROM ");
            sb.append("                         ( ");
            sb.append("                                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("                                ,ds ");
            sb.append("                                 FROM " + dwdTableNameF + " WHERE ds='" + SystemConstant.CURTIME + "' - 1 ");
            sb.append("                                 UNION ALL ");
            sb.append("                                SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("                                 ,ds ");
            sb.append("                                FROM " + odsTableName + " WHERE ds='" + SystemConstant.CURTIME + "' ");
            sb.append("                         ) a ");
            sb.append("                 GROUP BY " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("         ) b ");
            sb.append(" WHERE cnt=1; ");
            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS SELECT * FROM ");
            sb.append("         ( SELECT ");
            sb.append("                 tmp_at.* ");
            sb.append("                 ,atc.cnt as cnt2 ");
            sb.append("                ,CASE WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
            sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + "' THEN 'insert' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + " - 1' THEN 'delete' ");
            sb.append(" END AS action ");
            sb.append(" FROM tmp_at_" + odsTableName + " tmp_at LEFT JOIN ( SELECT " + CommonFieldEnum.S_KEY.getCode() + ",COUNT(1) as cnt FROM tmp_at_" + odsTableName + " GROUP BY " + CommonFieldEnum.S_KEY.getCode() + " ) atc ON tmp_at." + CommonFieldEnum.S_KEY.getCode() + "=atc." + CommonFieldEnum.S_KEY.getCode() + " ) tb_u ");
            sb.append(" WHERE action <> 'his'; ");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameI + " PARTITION (ds='" + SystemConstant.CURTIME + "') ");
            sb.append(" SELECT ");
            sb.append("        record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append("        ,   record." + CommonFieldEnum.S_SRC.getCode());
            sb.append("        ,   record." + AddFieldEnum.S_ACTION.getCode());
            sb.append(" FROM ");
            sb.append("         ( ");
            sb.append("                 SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append("                 ,   CONCAT('" + sysCode + "_',s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
            sb.append("                 ,   action as " + AddFieldEnum.S_ACTION.getCode());
            sb.append("                FROM tb_update_" + odsTableName + ") ");
            sb.append(" record; ");
            sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
            sb.append(" SELECT  record." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            sb.append("        ,record." + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append("         ,CASE    WHEN record.rm=1 and tb_update.id IS NOT NULL THEN tb_update.max_s_sdt ");
            sb.append(" ELSE record." + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append("         ,CASE    WHEN record.rm=1 and tb_update.id IS NOT NULL THEN '0' ");
            sb.append(" ELSE " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" END AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append("         ,record." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ( ");
            sb.append("         SELECT  *,row_number() over (partition by " + CommonFieldEnum.S_KEY.getCode() + " order by " + ZipperFieldEnum.S_END_TIME.getCode() + " desc) as rm ");
            sb.append("        FROM " + dwdTableNameF);
            sb.append("         WHERE   ds = '" + SystemConstant.CURTIME + "' - 1 ");
            sb.append(" ) record LEFT ");
            sb.append(" JOIN    tb_update_" + odsTableName + " tb_update");
            sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() + " = tb_update." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" UNION ");
            sb.append(" SELECT  di." + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns.replace(",record.s_sdt", "").replace(",record.S_SDT", "").replace("record.", "di."));
            sb.append("        ,tb_update.max_s_sdt as s_sdt ");
            sb.append(",CASE ");
            if (noHeadColumns.contains(SystemConstant.UPDATETIME)) {
                sb.append(" WHEN tb_update.action = 'insert' and tb_update." + SystemConstant.UPDATETIME + " IS NOT NULL THEN tb_update." + SystemConstant.UPDATETIME);
            }
            if (noHeadColumns.contains(SystemConstant.CREATETIME)) {
                sb.append("  WHEN tb_update.action = 'insert' AND tb_update." + SystemConstant.CREATETIME + " IS NOT NULL THEN tb_update." + SystemConstant.CREATETIME);
            }
            sb.append("  WHEN tb_update.action = 'insert' THEN '19710101000000' ");
            sb.append(" ELSE tb_update.max_s_sdt ");
            sb.append(" END as " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append("        ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append("        ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append("        ,di." + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM  tb_update_" + odsTableName + "   tb_update ");
            sb.append(" LEFT JOIN (SELECT * FROM " + dwdTableNameI +" WHERE ds='" + SystemConstant.CURTIME + "') di ");
            sb.append(" ON tb_update." + CommonFieldEnum.S_KEY.getCode() + "=di." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" WHERE   tb_update.action = 'update' ");
            sb.append(" OR      tb_update.action = 'insert'; ");
            sb.append(" DROP TABLE tmp_at_" + odsTableName + "; ");
            sb.append(" DROP TABLE tb_update_" + odsTableName + "; ");
        }

        return sb.toString();
    }
}
