package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.constants.SystemConstant;
import com.alex.springcloud.entity.SqlInfo;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.entity.SysDict;
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
   private SysDictService sysDictService;

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
        if(file==null)
            throw new Exception("文件不能为空！");
        List<SqlInfo> list = new ArrayList<>();
        ExcelImportResult<SqlInfoImport> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0)
            index = 0;
        QueryWrapper<SqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_code", dwdSysCode);
        queryWrapper.eq("belong_to", belongTo);
        this.baseMapper.delete(queryWrapper);
        SysDict zipper = sysDictService.findSysDict(dwdSysCode, "is_zipper", belongTo);
        SysDict dwdCodes = sysDictService.findSysDict(dwdSysCode, "sys_code", belongTo);
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
                    throw new Exception("表名不能为空");
                odsTableName = odsTableName.toLowerCase().trim();
                if (StringUtils.isBlank(odsTableName))
                    throw new Exception("表名不能为空");
                String odsTableNameCn = result.getWorkbook().getSheetName(index);
                String dwdTableNameI = "";
                String dwdTableNameCnI = "";
                String dwdTableNameF = "";
                String dwdTableNameCnF = "";
                //根据表名的最后一位判断表是事实表还是维度表,如果是事实表生成事实表全量名
                String info = odsTableName.substring(odsTableName.length() - 1);
                String[] isZipper = null;
                if (zipper != null && zipper.getValue() != null)
                    isZipper = zipper.getValue().split(",");
                start++;
                Integer isZ = isZipper == null ? 1 : start < isZipper.length ? Integer.parseInt(isZipper[start]) : 1;
                String[] s = odsTableName.split("_");
                StringBuilder suffixName = new StringBuilder();
                String[] dwdCode = null;
                if (dwdCodes != null && dwdCodes.getValue() != null)
                    dwdCode = dwdCodes.getValue().split(",");
                suffixName.append("_" + dwdSysCode + (dwdCode == null ? "" : (start < dwdCode.length ? dwdCode[start] : "")));
                for(int i = 2; i < s.length; i++)
                    suffixName.append("_" + s[i]);
                if (SystemConstant.ADD_TABLE.equals(info)) {
                    dwdTableNameI = SystemConstant.DWD + suffixName.toString();
                    dwdTableNameCnI = SystemConstant.DWD_UP + odsTableNameCn.substring(3);
                    int last = dwdTableNameI.lastIndexOf("_");
                    String tail = dwdTableNameI.substring(last);
                    dwdTableNameF = dwdTableNameI.substring(0, last) + tail.replace(SystemConstant.ADD_TABLE, SystemConstant.FULL_TABLE);
                    dwdTableNameCnF = dwdTableNameCnI.substring(0, dwdTableNameCnI.length() - 2) + SystemConstant.FULL_CN;
                } else if (SystemConstant.FULL_TABLE.equals(info)){
                    dwdTableNameF = SystemConstant.DIM + suffixName.toString();
                    dwdTableNameCnF = SystemConstant.DIM_UP + odsTableNameCn.substring(3);
                }
                sqlInfo.setDwdZipperTableName(dwdTableNameF);
                sqlInfo.setDwdAddTableName(dwdTableNameI);
                sqlInfo.setTableName(odsTableName);
                sqlInfo.setTableNameCn(odsTableNameCn);
                sqlInfo.setSysCode(dwdSysCode);
                sqlInfo.setBelongTo(belongTo);
                sqlInfo.setOdsSql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.ODS, isZ));
                sqlInfo.setOdsSqlMysql(tableSqlService.setSql(result.getList(), odsTableName, odsTableNameCn, SystemConstant.NOR_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.ODS, isZ));
                if (SystemConstant.ADD_TABLE.equals(info)) {
                    sqlInfo.setDwdSqlAdd(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MAX_COMPUTE, SystemConstant.DWD, isZ));
                    sqlInfo.setDwdSqlAddMysql(tableSqlService.setSql(result.getList(), dwdTableNameI, dwdTableNameCnI, SystemConstant.ADD_TYPE, SystemConstant.MYSQL_TYPE, SystemConstant.DWD, isZ));
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.ADD_TABLE, isZ, odsPrefix));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.ADD_TABLE, isZ, odsPrefix));
                } else {
                    sqlInfo.setOdsToDwdInitSql(odsToDwdSqlService.setOdsToDwdInitSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.FULL_TABLE, isZ, odsPrefix));
                    sqlInfo.setOdsToDwdSql(odsToDwdSqlService.setOdsToDwdSql(result.getList(), odsTableName, dwdTableNameI, dwdTableNameF, SystemConstant.FULL_TABLE, isZ, odsPrefix));
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
