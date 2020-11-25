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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class SqlInfoService extends ServiceImpl<SqlInfoMapper, SqlInfo> {

    @Autowired
    private TableSqlService tableSqlService;

    @Autowired
    private OdsToDwdSqlService odsToDwdSqlService;

    public List<SqlInfo> importInfo(MultipartFile file, Integer index, Integer... isZipper) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, index, isZipper);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    /**
     * @param file        导入的文件信息
     * @param index       开始识别的sheet页
     * @param isZipper    是否是拉链表信息
     * @description:      根据导入文件生成sql
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
                String odsTableName = result.getList().get(0).getTableName();
                if (StringUtils.isBlank(odsTableName))
                    throw new Exception("表明不能为空");
                odsTableName = odsTableName.toLowerCase().trim();
                if (StringUtils.isBlank(odsTableName))
                    throw new Exception("表明不能为空");
                String odsTableNameCn = result.getWorkbook().getSheetName(index);
                String dwdTableNameI = null;
                String dwdTableNameCnI = null;
                String dwdTableNameF;
                String dwdTableNameCnF;
                //根据表名的最后一位判断表是事实表还是维度表,如果是事实表生成事实表全量名
                String info = odsTableName.substring(odsTableName.length() - 1);
                if (SystemConstant.ADD_TABLE.equals(info)) {
                    dwdTableNameI = SystemConstant.DWD + odsTableName.substring(3);
                    dwdTableNameCnI = SystemConstant.DWD_UP + odsTableNameCn.substring(3);
                    int last = dwdTableNameI.lastIndexOf("_");
                    String tail = dwdTableNameI.substring(last);
                    dwdTableNameF = dwdTableNameI.substring(0, last) + tail.replace(SystemConstant.ADD_TABLE, SystemConstant.FULL_TABLE);
                    dwdTableNameCnF = dwdTableNameCnI.substring(0, dwdTableNameCnI.length() - 2) + SystemConstant.FULL_CN;
                } else {
                    dwdTableNameF = SystemConstant.DIM + odsTableName.substring(3);
                    dwdTableNameCnF = SystemConstant.DIM_UP + odsTableNameCn.substring(3);
                }
                sqlInfo.setTableName(odsTableName);
                sqlInfo.setTableNameCn(odsTableNameCn);
                sqlInfo.setVersion(version);
                Integer isZ = isZipper == null ? 1 : ++start < isZipper.length ? isZipper[start] : 1;
                sqlInfo.setOdsSql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.ODS, isZ));
                sqlInfo.setOdsSqlMysql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.ODS, isZ));
                if (SystemConstant.ADD_TABLE.equals(info)) {
                    sqlInfo.setDwdSqlAdd(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD, isZ));
                    sqlInfo.setDwdSqlAddMysql(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD, isZ));
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.ADD_TABLE));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.ADD_TABLE));
                } else {
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.FULL_TABLE));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.FULL_TABLE));
                }
                sqlInfo.setDwdSqlZipper(tableSqlService.setSql(result.getList(), dwdTableNameF, dwdTableNameCnF, SystemConstant.ZIPPER_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD, isZ));
                sqlInfo.setDwdSqlZipperMysql(tableSqlService.setSql(result.getList(), dwdTableNameF, dwdTableNameCnF, SystemConstant.ZIPPER_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD, isZ));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }
}
