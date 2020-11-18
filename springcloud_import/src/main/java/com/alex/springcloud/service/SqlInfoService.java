package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfo;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.mapper.SqlInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class SqlInfoService extends ServiceImpl<SqlInfoMapper, SqlInfo> {

    public List<SqlInfo> importInfo(String type, MultipartFile file, Integer index, String isFact) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, type, index, isFact);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    /**
     * @param file      文件
     * @param type      类型（ads/dws/dwd/ods）
     * @param index     开始sheet页
     * @param isFact    是否是事实表
     * @description:    生成sql信息
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.SqlInfo>
     */
    private List<SqlInfo> importSql(MultipartFile file, String type, Integer index, String isFact) throws Exception {
        if(file==null)
            throw new Exception("文件不能为空！");
        List<SqlInfo> list = new ArrayList<>();
        ExcelImportResult<SqlInfoImport> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0)
            index = 0;
        QueryWrapper<SqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(version) as version").eq("type", type);
        List<SqlInfo> sqlInfos = this.baseMapper.selectList(queryWrapper);
        int version = sqlInfos == null || sqlInfos.size() == 0 || sqlInfos.get(0) == null ? 1: sqlInfos.get(0).getVersion() + 1;
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
                sqlInfo.setType(type);
                sqlInfo.setIsFact(isFact);
                String tableName = result.getList().get(0).getTableName();
                String tableNameCn = result.getWorkbook().getSheetName(index);
                sqlInfo.setTableName(tableName);
                sqlInfo.setTableNameCn(tableNameCn);
                sqlInfo.setVersion(version);
                if (SystemConstant.ADD_CN.equals(tableNameCn.trim().substring(tableNameCn.length() - 2))) {
                    sqlInfo.setSqlAdd(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ADD_TYPE, SystemConstant.MAX_COMPUTE));
                    sqlInfo.setSqlAddMysql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ADD_TYPE, SystemConstant.MYSQL_TYPE));
                }
                sqlInfo.setSqlZipper(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ZIPPER_TYPE, SystemConstant.MAX_COMPUTE));
                sqlInfo.setSqlZipperMysql(setSql(result.getList(), tableName, tableNameCn, SystemConstant.ZIPPER_TYPE, SystemConstant.MYSQL_TYPE));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }


    private String setSql(List<SqlInfoImport> list, String tableName, String tableNameCn, String type, String database) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        if (SystemConstant.ZIPPER_TYPE.equals(type)) {
            int last = tableName.lastIndexOf("_");
            String tail = tableName.substring(last);
            tableName = tableName.substring(0, last) + tail.replace("i", "f");
        }
        stringBuilder.append(tableName +"` (");
        if (SystemConstant.ADD_TYPE.equals(type)) {
            for (SqlInfoImport sqlInfoImport : list) {
                if (!SystemConstant.ADD_TABLE.contains(sqlInfoImport.getColumn())) {
                    stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
                    stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
                    stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
                }
            }
        } else if (SystemConstant.ZIPPER_TYPE.equals(type)) {
            for (SqlInfoImport sqlInfoImport : list) {
                if (!SystemConstant.ZIPPER_TABLE.contains(sqlInfoImport.getColumn())) {
                    stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
                    stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
                    stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
                }
            }
        } else {
            for (SqlInfoImport sqlInfoImport : list) {
                stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
                stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
                stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
            }
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        if (SystemConstant.ZIPPER_TYPE.equals(type))
            tableNameCn = tableNameCn.substring(0, tableNameCn.length() - 2) + "全量";
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
