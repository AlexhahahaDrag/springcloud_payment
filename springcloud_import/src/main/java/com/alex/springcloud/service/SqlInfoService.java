package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.GatherImportInfo;
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

    @Autowired
    private OdsTestSqlService odsTestSqlService;

    /**
     * @param file          导入的文件信息
     * @param index         开始识别的sheet页
     * @param dwdSysCode    dwd系统编号
     * @param odsPrefix     ods标准表前缀
     * @description:
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.SqlInfo>
     */
    public List<SqlInfo> importInfo(MultipartFile file, Integer index, String dwdSysCode, String odsPrefix, String belongTo) throws Exception {
        List<SqlInfo> sqlInfos = importSql(file, index, dwdSysCode, odsPrefix, belongTo);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    /**
     * @param file        导入的文件信息
     * @param index       开始识别的sheet页
     * @param dwdSysCode  dwd系统编号
     * @param odsPrefix   ods标准表前缀
     * @description:      根据导入文件生成sql
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.SqlInfo>
     */
    private List<SqlInfo> importSql(MultipartFile file, Integer index, String dwdSysCode, String odsPrefix, String belongTo) throws Exception {
        List<GatherImportInfo> firstSheet = getFirstSheet(file);
        if(file==null)
            throw new Exception("文件不能为空！");
        //首页不能为空
        if (firstSheet == null || firstSheet.isEmpty())
            throw new Exception("首页不能为空！");;
        return getTableInfo(firstSheet, file, index, dwdSysCode, odsPrefix, belongTo);
    }

    private List<SqlInfo> getTableInfo(List<GatherImportInfo> firstSheet, MultipartFile file, Integer index, String dwdSysCode, String odsPrefix, String belongTo) throws Exception {
        List<SqlInfo> list = new ArrayList<>();
        ExcelImportResult<SqlInfoImport> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0)
            index = 0;
        //物理删除之前创建的数据
        QueryWrapper<SqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_code", dwdSysCode);
        queryWrapper.eq("belong_to", belongTo);
        this.baseMapper.delete(queryWrapper);
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
                String odsTableName = "";
                String odsTableNameCn = "";
                //dwd表增量名
                String dwdTableNameI = "";
                String dwdTableNameCnI = "";
                //dwd表全量名
                String dwdTableNameF = "";
                String dwdTableNameCnF = "";
                //greenplum表名
                String greenTableName = "";
                String greenTableNameCn = "";
                //表类型
                String tableType = "";
                //表同步方式
                String tableSysnWay = "";
                //dwd表前缀名
                StringBuilder suffixName = new StringBuilder();
                //创建时间
                String[] createTimes = null;
                //更新时间
                String[] updateTimes = null;
                //根据表名的最后一位判断表是事实表还是维度表,如果是事实表生成事实表全量名
                GatherImportInfo gatherImportInfo = firstSheet.get(++start);
                if (gatherImportInfo != null) {
                    tableType = gatherImportInfo.getTableType();
                    tableSysnWay = gatherImportInfo.getSysnWay();
                    odsTableName = gatherImportInfo.getTableName();
                    odsTableNameCn = gatherImportInfo.getTableNameCn();
                    suffixName.append("_" + dwdSysCode + (gatherImportInfo.getDwdTableNo() == null ? "" : gatherImportInfo.getDwdTableNo()));
                    String[] s = odsTableName.split("_");
                    for(int i = 2; i < s.length; i++)
                        suffixName.append("_" + s[i]);
                    if (SystemConstant.FACT_TABLE.equals(tableType) && SystemConstant.ADD_CN.equals(tableSysnWay)) {
                        if (StringUtils.isNotBlank(gatherImportInfo.getCreateTime()))
                            createTimes = gatherImportInfo.getCreateTime().split("\\/");
                        if (StringUtils.isNotBlank(gatherImportInfo.getUpdateTime()))
                            updateTimes = gatherImportInfo.getUpdateTime().split("\\/");
                    }
                }
                if (SystemConstant.FACT_TABLE.equals(tableType)) {
                    if (odsTableName.charAt(odsTableName.length() - 1) == 'i') {
                        dwdTableNameI = SystemConstant.DWD + suffixName.toString();
                        dwdTableNameCnI = SystemConstant.DWD_UP + odsTableNameCn.substring(3);
                        int last = dwdTableNameI.lastIndexOf("_");
                        String tail = dwdTableNameI.substring(last);
                        dwdTableNameF = dwdTableNameI.substring(0, last) + tail.replace(SystemConstant.ADD_TABLE, SystemConstant.FULL_TABLE);
                        dwdTableNameCnF = dwdTableNameCnI.substring(0, dwdTableNameCnI.length() - 2) + SystemConstant.FULL_CN;
                    } else if (odsTableName.charAt(odsTableName.length() - 1) == 'f') {
                        dwdTableNameF = SystemConstant.DWD + suffixName.toString();
                        dwdTableNameCnF = SystemConstant.DWD_UP + odsTableNameCn.substring(3);
                        int last = dwdTableNameF.lastIndexOf("_");
                        String tail = dwdTableNameF.substring(last);
                        dwdTableNameI = dwdTableNameF.substring(0, last) + tail.replace(SystemConstant.FULL_TABLE, SystemConstant.ADD_TABLE);
                        dwdTableNameCnI = dwdTableNameCnF.substring(0, dwdTableNameCnF.length() - 2) + SystemConstant.ADD_CN;
                    }
                } else if (SystemConstant.DIMENSION_TABLE.equals(tableType)){
                    dwdTableNameF = SystemConstant.DIM + suffixName.toString();
                    dwdTableNameCnF = SystemConstant.DIM_UP + odsTableNameCn.substring(3);
                } else if (SystemConstant.DICTIONARY_TABLE.equals(tableType)){
                    dwdTableNameF = SystemConstant.DIM + suffixName.toString();
                    dwdTableNameCnF = SystemConstant.DIM_UP + odsTableNameCn.substring(3);
                }
                greenTableName = StringUtils.isEmpty(gatherImportInfo.getGreenTableName()) ? dwdTableNameF : gatherImportInfo.getGreenTableName();
                greenTableNameCn = StringUtils.isEmpty(gatherImportInfo.getGreenTableNameCn()) ? dwdTableNameCnF : gatherImportInfo.getGreenTableNameCn();
                sqlInfo.setDwdZipperTableName(dwdTableNameF);
                sqlInfo.setDwdAddTableName(dwdTableNameI);
                sqlInfo.setTableName(odsTableName);
                sqlInfo.setTableNameCn(odsTableNameCn);
                sqlInfo.setSysCode(dwdSysCode);
                sqlInfo.setBelongTo(belongTo);
                sqlInfo.setSqlZipperGreenplumName(greenTableName);
                sqlInfo.setOdsSql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.ODS));
                sqlInfo.setOdsSqlMysql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.ODS));
                if (SystemConstant.FACT_TABLE.equals(tableType)) {
                    sqlInfo.setDwdSqlAdd(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD));
                    sqlInfo.setDwdSqlAddMysql(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD));
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                } else if (SystemConstant.DIMENSION_TABLE.equals(tableType)){
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                } else if (SystemConstant.DICTIONARY_TABLE.equals(tableType)) {
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, odsPrefix, createTimes, updateTimes, tableType, tableSysnWay));
                }
                sqlInfo.setDwdSqlZipper(tableSqlService.setSql(result.getList(), dwdTableNameF, dwdTableNameCnF, SystemConstant.ZIPPER_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD));
                sqlInfo.setDwdSqlZipperMysql(tableSqlService.setSql(result.getList(), dwdTableNameF, dwdTableNameCnF, SystemConstant.ZIPPER_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD));
                sqlInfo.setSqlZipperGreenplum(tableSqlService.setGreenplumSql(result.getList(), greenTableName, greenTableNameCn));
                sqlInfo.setOdsTestSql(odsTestSqlService.setOdsTestSql(result.getList(), odsTableName));
                list.add(sqlInfo);
            }
            index++;
        }
        return list;
    }

    private List<GatherImportInfo> getFirstSheet(MultipartFile file) throws Exception {
        ImportParams importParams = new ImportParams();
        //设置导入位置
        importParams.setHeadRows(1);
        importParams.setTitleRows(0);
        importParams.setStartRows(0);
        importParams.setStartSheetIndex(0);
        return ExcelImportUtil.importExcel(file.getInputStream(), GatherImportInfo.class, importParams);
    }
}
