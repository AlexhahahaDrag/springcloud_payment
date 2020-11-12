package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
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

    public List<SqlInfo> importInfo(String type, MultipartFile file, Integer index) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, type, index);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    private List<SqlInfo> importSql(MultipartFile file, String type, Integer index) throws Exception {
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
                String tableName = result.getList().get(0).getTableName();
                String tableNameCn = result.getWorkbook().getSheetName(index);
                sqlInfo.setTableName(tableName);
                sqlInfo.setTableNameCn(tableNameCn);
                sqlInfo.setVersion(version);
                sqlInfo.setSqlMc(getSql(result.getList(), tableName, tableNameCn));
                sqlInfo.setSqlMysql(setSqlMysql(result.getList(), tableName, tableNameCn));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }

    private String setSqlMysql(List<SqlInfoImport> list, String tableName, String tableNameCn) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        stringBuilder.append(tableName +"` (");
        for (SqlInfoImport sqlInfoImport : list) {
            stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
            stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
            stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        stringBuilder.append(") COMMENT = '" + tableNameCn + "';");
        return stringBuilder.toString();
    }

    private String getSql(List<SqlInfoImport> list, String tableName, String tableNameCn) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        stringBuilder.append(tableName +"` (");
        for (SqlInfoImport sqlInfoImport : list) {
            stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
            stringBuilder.append(sqlInfoImport.getColumnType() == null ? "string" : sqlInfoImport.getColumnType());
            stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        stringBuilder.append(") COMMENT '" + tableNameCn + "'");
        stringBuilder.append(" PARTITIONED BY (ds string COMMENT '业务日期');");
        return stringBuilder.toString();
    }
}
