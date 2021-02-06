package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.entity.GPSqlInfo;
import com.alex.springcloud.entity.GPSqlInfoImport;
import com.alex.springcloud.entity.SqlInfoImport;
import com.alex.springcloud.mapper.GPSqlInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/6 9:23
 * @version:     1.0
 */
@Service
public class GPSqlInfoService extends ServiceImpl<GPSqlInfoMapper, GPSqlInfo> {

    @Autowired
    private GPSqlService gpSqlService;

    /**
     * @param file     文件
     * @param schema   schema名
     * @param index    文件初始sheet页
     * @param belongTo 属于
     * @description:   根据导入的汇总表生成建表语句
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.GPSqlInfo>
     */
    public List<GPSqlInfo> importGPInfo(MultipartFile file, String schema, Integer index, String belongTo) throws Exception {
        List<GPSqlInfo> sqlInfos = getTableInfo(file, schema, index, belongTo);
        if (sqlInfos != null && sqlInfos.size() > 0)
            this.saveBatch(sqlInfos);
        return sqlInfos;
    }

    /**
     * @param file     文件
     * @param schema   schema名
     * @param index    文件初始sheet页
     * @param belongTo 属于
     * @description:
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.GPSqlInfo>
     */
    private List<GPSqlInfo> getTableInfo(MultipartFile file, String schema, Integer index, String belongTo) throws Exception {
        ExcelImportResult<GPSqlInfoImport> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0)
            index = 0;
        //物理删除之前创建的数据
        QueryWrapper<GPSqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_to", belongTo);
        this.baseMapper.delete(queryWrapper);
        List<GPSqlInfo> res = new ArrayList();
        while (index < sheets) {
            ImportParams importParams = new ImportParams();
            //设置导入位置
            importParams.setHeadRows(1);
            importParams.setTitleRows(0);
            importParams.setStartRows(0);
            importParams.setStartSheetIndex(index);
            result = ExcelImportUtil.importExcelMore(file.getInputStream(), GPSqlInfoImport.class, importParams);
            sheets =  result.getWorkbook().getNumberOfSheets();
            if (result != null && result.getList() != null && result.getList().size() > 0) {
                List<GPSqlInfoImport> list = result.getList();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    GPSqlInfoImport gpSqlInfoImport = list.get(i);
                    if (gpSqlInfoImport.getColumn() == null || "字段名".equals(gpSqlInfoImport.getColumn()))
                        continue;
                    if (gpSqlInfoImport.getColumn().startsWith("dws_")) {
                        GPSqlInfo gpSqlInfo = new GPSqlInfo();
                        gpSqlInfo.setBelongTo(belongTo);
                        String tableName = gpSqlInfoImport.getColumn().substring(0, (gpSqlInfoImport.getColumn().indexOf("(") != -1 ? gpSqlInfoImport.getColumn().indexOf("(")
                                : (gpSqlInfoImport.getColumn().indexOf("（")) != -1 ? gpSqlInfoImport.getColumn().indexOf("（") : gpSqlInfoImport.getColumn().length()));
                        gpSqlInfo.setTableName(tableName);
                        gpSqlInfo.setColumnList(new ArrayList<>());
                        while(i + 1 < size && list.get(i + 1).getColumn() != null && !list.get(i + 1).getColumn().startsWith("dws_")) {
                            if (!"字段名".equals(list.get(i + 1).getColumn()))
                                gpSqlInfo.getColumnList().add(list.get(i + 1));
                            i++;
                        }
                        gpSqlInfo.setTableSql(gpSqlService.setGreenplumSql(gpSqlInfo.getColumnList(), tableName,
                                gpSqlInfoImport.getColumn().substring((gpSqlInfoImport.getColumn().indexOf("(") != -1 ? gpSqlInfoImport.getColumn().indexOf("(") :
                                        (gpSqlInfoImport.getColumn().indexOf("（") != -1 ? gpSqlInfoImport.getColumn().indexOf("（") : -1))  + 1, gpSqlInfoImport.getColumn().length() - 1), schema));
                        res.add(gpSqlInfo);
                    }

                }
            }
            index++;
        }
        return res;
    }
}
