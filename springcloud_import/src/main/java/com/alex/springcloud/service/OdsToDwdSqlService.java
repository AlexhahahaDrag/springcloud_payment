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
     * @param type            传入类型（全量、增量）
     * @description:  生成ods到dwd的初始化sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdInitSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF,
                                     String type, Integer isZipper, String odsPrefix, String[] createTimes, String[] updateTimes) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.ADD_TABLE.equals(type))
            return setOdsToDwdInitSqlAdd(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode, isZipper, odsPrefix, createTimes, updateTimes);
        else if (SystemConstant.FULL_TABLE.equals(type))
            return setOdsToDwdInitSqlFull(noHeadColumns.toString(), odsTableName, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes);
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
    public String setOdsToDwdInitSqlFull(String noHeadColumns, String odsTableName, String dwdTableNameF, String sysCode, String odsPrefix, String[] createTimes, String[] updateTimes) {
        boolean flag = (createTimes != null && createTimes.length > 0) || (updateTimes != null && updateTimes.length > 0);
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF + " PARTITION(ds='" + SystemConstant.CURTIME + "') ");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode());
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
        sb.append(" ,CONCAT(' " + sysCode + "_',s_org_code) as " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
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
    public String setOdsToDwdInitSqlAdd(String columns, String noHeadColumns, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode, Integer isZipper, String odsPrefix, String[] createTimes, String[] updateTimes) {
        boolean flag = (updateTimes != null && updateTimes.length > 0) || (createTimes != null && createTimes.length > 0);
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
        sb.append(" FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "') record;");
        sb.append(" INSERT OVERWRITE TABLE " + dwdTableNameF +" PARTITION(ds='" + SystemConstant.CURTIME + "')");
        sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
        sb.append(noHeadColumns);
        if (isZipper == 0) {
            sb.append(" ,null as " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,null as " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,null as " + ZipperFieldEnum.S_STAT.getCode());
        } else {
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
        }
        sb.append("," + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM " + dwdTableNameI);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "';");
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
     * @param type            传入类型（全量、增量）
     * @param isZipper        是否是拉链
     * @param odsPrefix       ods标准所在空间
     * @description:          生成ods到dwd的同步sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSql(List<SqlInfoImport> list, String odsTableName, String dwdTableNameI, String dwdTableNameF, String type,
                                 Integer isZipper, String odsPrefix, String[] createTimes, String[] updateTimes) {
        StringBuilder columns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            columns.append(",record." + sqlInfoImport.getColumn());
        StringBuilder noHeadColumns = new StringBuilder();
        for (SqlInfoImport sqlInfoImport : list)
            noHeadColumns.append("," + sqlInfoImport.getColumn());
        String sysCode = odsTableName.split("_")[1];
        if (SystemConstant.ADD_TABLE.equals(type))
            return setOdsToDwdSqlAdd(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameI, dwdTableNameF, sysCode, isZipper, odsPrefix, createTimes, updateTimes);
        else if (SystemConstant.FULL_TABLE.equals(type))
            return setOdsToDwdSqlFull(columns.toString(), noHeadColumns.toString(), odsTableName, dwdTableNameF, sysCode, odsPrefix, createTimes, updateTimes);
        return "";
    }

    /**
     * @param columns         有表头列信息（record.id,..）
     * @param noHeadColumns   ods表名
     * @param odsTableName    dwd增量事实表名
     * @param dwdTableNameF   dwd全量事实表名
     * @param sysCode         系统编号（ods表名ods_后到下一个_的数据）
     * @param odsPrefix       ods标准所在空间
     * @description:          生成ods到dwd维度表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlFull(String columns, String noHeadColumns, String odsTableName, String dwdTableNameF, String sysCode, String odsPrefix, String[] createTimes, String[] updateTimes) {
        StringBuilder sb = new StringBuilder();
        sb.append(" WITH at_" + odsTableName + " AS ( ");
        sb.append(" SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode() + ",");
        sb.append(noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", "").substring(1));
        sb.append(",cnt, max_s_sdt, max_ds, row_number() over (partition by id order by max_ds desc) as rm FROM ");
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
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "' - 1 ");
        sb.append("                        UNION ALL ");
        sb.append("                        SELECT * FROM ");
        if (StringUtils.isNotBlank(odsPrefix)) {
            sb.append(odsPrefix + ".");
        }
        sb.append(odsTableName);
        sb.append(" WHERE ds='" + SystemConstant.CURTIME + "' ");
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
        if (updateTimes != null && updateTimes.length > 0) {
            for (String updateTime : updateTimes) {
                String newUpdateTime = dealTime(updateTime);
                sb.append(" WHEN action = 'insert' and " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
            }
        }
        if (createTimes != null && createTimes.length > 0) {
            for (String createTime : createTimes) {
                String newCreateTime = dealTime(createTime);
                sb.append("  WHEN action = 'insert' AND " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
            }
        }
        sb.append("  WHEN action = 'insert' THEN '19710101000000' ");
        sb.append("  ELSE max_s_sdt ");
        sb.append(" END as " + ZipperFieldEnum.S_START_TIME.getCode());
        sb.append("         ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
        sb.append("        ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
        sb.append(" ,CONCAT('" + sysCode + "_',s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
        sb.append(" FROM    tb_update_" + odsTableName);
        sb.append(" WHERE action = 'update' ");
        sb.append(" OR action = 'insert'; ");
        return sb.toString();
    }

    /**
     * @param columns          有表头列信息（record.id,..）
     * @param noHeadColumns    无表头列信息（id,..）
     * @param odsTableName     ods表名
     * @param dwdTableNameI    dwd增量事实表名
     * @param dwdTableNameF    dwd全量事实表名
     * @param sysCode          系统编号（ods表名ods_后到下一个_的数据）
     * @param isZipper         是否是拉链
     * @param odsPrefix        ods标准所在空间
     * @description:           生成ods到dwd事实表同步的sql
     * @author: alex
     * @return: java.lang.String
     */
    public String setOdsToDwdSqlAdd(String columns, String noHeadColumns, String odsTableName, String dwdTableNameI, String dwdTableNameF, String sysCode,
                                    Integer isZipper, String odsPrefix, String[] createTimes, String[] updateTimes) {
        StringBuilder sb = new StringBuilder();
        if (isZipper == 0) {
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_',s_org_code,'_',id) AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            sb.append(" ,'insert' AS " + AddFieldEnum.S_ACTION.getCode());
            sb.append(" FROM ");
            if (StringUtils.isNotBlank(odsPrefix)) {
                sb.append(odsPrefix + ".");
            }
            sb.append(odsTableName);
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
            sb.append(" ,null " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,null AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,null AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ,CONCAT('1020005_',s_org_code) AS  " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tmp_at_" + odsTableName + ";");
            sb.append(" DROP TABLE tmp_at_" + odsTableName + " ; ");
        } else if (isZipper == 1) {
            boolean flag = (createTimes != null && createTimes.length > 0) || (updateTimes != null && updateTimes.length > 0);
            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS ");
            sb.append(" SELECT  CONCAT('" + sysCode + "_', record.s_org_code, '_', record.id) AS " + CommonFieldEnum.S_KEY.getCode());
            sb.append(columns);
            if (flag)
                sb.append(" ,CASE ");
            if (createTimes != null && createTimes.length > 0) {
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append(" WHEN " + newCreateTime + " IS NOT NULL AND " + newCreateTime + " >= '" + SystemConstant.CURTIME + "' THEN 'insert' ");
                }
            }
            if (updateTimes != null && updateTimes.length > 0) {
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN " + newUpdateTime + " IS NOT NULL AND " + newUpdateTime + " >= '" + SystemConstant.CURTIME + "' THEN 'update' ");
                }
            }
            if (flag)
                sb.append(" END AS " + AddFieldEnum.S_ACTION.getCode());
            else
                sb.append("''" + AddFieldEnum.S_ACTION.getCode());
            sb.append("         ,CONCAT('" + sysCode + "_', record.s_org_code) AS " + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    ");
            if (StringUtils.isNotBlank(odsPrefix)) {
                sb.append(odsPrefix + ".");
            }
            sb.append(odsTableName);
            sb.append(" AS record ");
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
            sb.append("         ,record." + ZipperFieldEnum.S_START_TIME.getCode());
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
            sb.append(" ON      record." + CommonFieldEnum.S_KEY.getCode() +" = b." + CommonFieldEnum.S_KEY.getCode());
            sb.append(" UNION ");
            sb.append(" SELECT " + CommonFieldEnum.S_KEY.getCode());
            sb.append(noHeadColumns);
            if (flag)
                sb.append(" ,CASE ");
            if (updateTimes != null && updateTimes.length > 0) {
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN " + newUpdateTime + " IS NOT NULL THEN " + newUpdateTime);
                }
            }
            if (createTimes != null && createTimes.length > 0) {
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append(" WHEN " + newCreateTime + " IS NOT NULL THEN " + newCreateTime);
                }
            }
            if (flag)
                sb.append(" ELSE '19710101000000' END AS " + ZipperFieldEnum.S_START_TIME.getCode());
            else
                sb.append(" '19710101000000' AS " + ZipperFieldEnum.S_START_TIME.getCode());
            sb.append(" ,'20990101000000' AS " + ZipperFieldEnum.S_END_TIME.getCode());
            sb.append(" ,'1' AS " + ZipperFieldEnum.S_STAT.getCode());
            sb.append(" ," + CommonFieldEnum.S_SRC.getCode());
            sb.append(" FROM    tb_update_" + odsTableName);
            sb.append(" WHERE   " + AddFieldEnum.S_ACTION.getCode() + " = 'update' ");
            sb.append(" OR      " + AddFieldEnum.S_ACTION.getCode() +" = 'insert' ");
            sb.append("         ; ");
            sb.append(" DROP TABLE IF EXISTS tb_update_" + odsTableName + ";");
        } else if (isZipper == 2){
            sb.append(" CREATE TABLE tmp_at_" + odsTableName + " AS ");
            sb.append(" SELECT *,row_number() over (partition by id order by max_ds desc) as rm FROM ");
            sb.append("         ( ");
            sb.append("                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
            sb.append("                 ,count(1) as cnt,MAX(s_sdt) as max_s_sdt,MAX(ds) as max_ds ");
            sb.append("                FROM ");
            sb.append("                         ( ");
            sb.append("                                 SELECT " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("                                ,ds ");
            sb.append("                                 FROM " + dwdTableNameF + " WHERE ds='" + SystemConstant.CURTIME + "' - 1 ");
            sb.append("                                 UNION ALL ");
            sb.append("                                SELECT CONCAT('" + sysCode + "_',s_org_code,'_',id) as " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns);
            sb.append("                                 ,ds ");
            sb.append(" FROM ");
            if (StringUtils.isNotBlank(odsPrefix))
                sb.append(odsPrefix + ".");
            sb.append(odsTableName + " WHERE ds='" + SystemConstant.CURTIME + "' ");
            sb.append("                         ) a ");
            sb.append("                 GROUP BY " + CommonFieldEnum.S_KEY.getCode() + noHeadColumns.replace(",s_sdt", "").replace(",S_SDT", ""));
            sb.append("         ) b ");
            sb.append(" WHERE cnt=1; ");
            sb.append(" CREATE TABLE tb_update_" + odsTableName + " AS SELECT * FROM ");
            sb.append("         ( SELECT ");
            sb.append("                 tmp_at.* ");
            sb.append("                 ,atc.cnt as cnt2 ");
            sb.append("                ,CASE WHEN atc.cnt=2 AND rm=2 THEN 'his' ");
            sb.append(" WHEN atc.cnt=2 AND rm=1 THEN 'update' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + "' THEN 'insert' ");
            sb.append(" WHEN atc.cnt=1 AND max_ds='" + SystemConstant.CURTIME + "' - 1 THEN 'delete' ");
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
            sb.append(noHeadColumns.replace(",s_sdt", ",max_s_sdt as s_sdt").replace(",S_SDT", ",max_s_sdt as s_sdt"));
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
            sb.append(columns.replace(",record.s_sdt", ",record.max_s_sdt as s_sdt ").replace(",record.S_SDT", ",record.max_s_sdt as s_sdt ").replace("record.", "di."));
            sb.append(",CASE ");
            if (updateTimes != null && updateTimes.length > 0) {
                for (String updateTime : updateTimes) {
                    String newUpdateTime = dealTime(updateTime);
                    sb.append(" WHEN tb_update.action = 'insert' and tb_update." + newUpdateTime + " IS NOT NULL THEN tb_update." + newUpdateTime);
                }
            }
            if (createTimes != null && createTimes.length > 0) {
                for (String createTime : createTimes) {
                    String newCreateTime = dealTime(createTime);
                    sb.append("  WHEN tb_update.action = 'insert' AND tb_update." + newCreateTime + " IS NOT NULL THEN tb_update." + newCreateTime);
                }
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
