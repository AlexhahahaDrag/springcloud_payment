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

    public boolean importInfo(String type, MultipartFile file, Integer index) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, type, index);
        this.saveBatch(sqlInfos);
        return true;
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
            if (result != null) {
                SqlInfo sqlInfo = new SqlInfo();
                sqlInfo.setType(type);
                String tableName = result.getWorkbook().getSheetName(index);
                sqlInfo.setTableName(tableName);
                sqlInfo.setVersion(version);
                sqlInfo.setSqlText(getSql(result.getList(), tableName));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }

    private String getSql(List<SqlInfoImport> list, String tableName) {
        if (list == null || list.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS `");
        stringBuilder.append(list.get(0).getTableName() +"` (");
        for (SqlInfoImport sqlInfoImport : list) {
            stringBuilder.append("`" + sqlInfoImport.getColumn() + "` ");
            stringBuilder.append(sqlInfoImport.getColumnType() == null ? "String" : sqlInfoImport.getColumnType());
            stringBuilder.append(" COMMENT '" + sqlInfoImport.getColumnName() + "',");
        }
        stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");
        stringBuilder.append(") COMMENT '" + tableName + "'");
        stringBuilder.append(" PARTITIONED BY (ds string COMMENT '业务日期');");
        return stringBuilder.toString();
    }
}
