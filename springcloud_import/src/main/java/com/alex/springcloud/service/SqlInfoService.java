package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfo;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.enums.CommonFieldEnum;
import com.alex.springcloud.mapper.SqlInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class SqlInfoService extends ServiceImpl<SqlInfoMapper, SqlInfo> {

    public List<SqlInfo> importInfo(MultipartFile file, Integer index, Integer... isZipper) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, index, isZipper);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    /**
     * @param file      文件
     * @param index      index sheet
     * @description:
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.SqlInfo>
     */
    private List<SqlInfo> importSql(MultipartFile file, Integer index, Integer... isZipper) throws Exception {
        if(file==null)
            throw new Exception("文件不能为空！");
        List<SqlInfo> list = new ArrayList<>();
        ExcelImportResult<SqlInfoImport> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0)
            index = 0;
        QueryWrapper<SqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(version) as version");
        List<SqlInfo> sqlInfos = this.baseMapper.selectList(queryWrapper);
        int version = sqlInfos == null || sqlInfos.size() == 0 || sqlInfos.get(0) == null ? 1: sqlInfos.get(0).getVersion() + 1;
        int start = -1;
        while (index < sheets) {
            ImportParams importParams = new ImportParams();
            //设置导入位置
            importParams.setHeadRows(1);
            importParams.setTitleRows(0);
            importParams.setStartRows(0);
            importParams.setStartSheetIndex(index);
            result = ExcelImportUtil.importExcelMore(file.getInputStream(), SqlInfoImport.class, importParams);
            sheets =  result.getWorkbook().getNumberOfSheets();
            if (result != null && result.getList() != null && result.getList().size() > 0) {
                SqlInfo sqlInfo = new SqlInfo();
                String tableName = result.getList().get(0).getTableName();
                String tableNameCn = result.getWorkbook().getSheetName(index);
                sqlInfo.setTableName(tableName);
                sqlInfo.setTableNameCn(tableNameCn);
                sqlInfo.setVersion(version);
                Integer isZ = isZipper == null ? 1 : ++start < isZipper.length ? isZipper[start] : 1;
                sqlInfo.setOdsSql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.ODS, isZ));
                sqlInfo.setOdsSqlMysql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.ODS, isZ));
                if (SystemConstant.ADD_CN.equals(tableNameCn.trim().substring(tableNameCn.length() - 2))) {
                    sqlInfo.setDwdSqlAdd(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ADD_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD, isZ));
                    sqlInfo.setDwdSqlAddMysql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ADD_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD, isZ));
                }
                sqlInfo.setDwdSqlZipper(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ZIPPER_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD, isZ));
                sqlInfo.setDwdSqlZipperMysql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ZIPPER_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD, isZ));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }


    private String setSql(List<SqlInfoImport> list, String tableName, String tableNameCn, String type, String database, String level, Integer isZ) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        if (SystemConstant.DWD.equals(level)) {
            //根据表中文名的最后两位判断表是事实表还是维度表
            if (SystemConstant.ADD_CN.equals(tableNameCn.trim().substring(tableNameCn.length() - 2))) {
                tableName = "dwd" + tableName.substring(3);
                tableNameCn = "DWD" + tableNameCn.substring(3);
                //增量表生成拉链表的时候会将增量字段变为全量字段
                if (SystemConstant.ZIPPER_TYPE.equals(type)) {
                    int last = tableName.lastIndexOf("_");
                    String tail = tableName.substring(last);
                    tableName = tableName.substring(0, last) + tail.replace("i", "f");
                    tableNameCn = tableNameCn.substring(0, tableNameCn.length() - 2) + "全量";
                }
            } else {
                tableName = "dim" + tableName.substring(3);
                tableNameCn = "DIM" + tableNameCn.substring(3);
            }
        }
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
        if (SystemConstant.ZIPPER_TYPE.equals(type) && isZ == 1) {
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
