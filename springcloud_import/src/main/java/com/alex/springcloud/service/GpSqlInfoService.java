package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.entity.GpSqlInfo;
import com.alex.springcloud.entity.GpSqlInfoImportBO;
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
public class GpSqlInfoService extends ServiceImpl<GPSqlInfoMapper, GpSqlInfo> {

    @Autowired
    private GpSqlService gpSqlService;

    /**
     * @param file     文件
     * @param schema   schema名
     * @param index    文件初始sheet页
     * @param belongTo 属于
     * @description:   根据导入的汇总表生成建表语句
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.GPSqlInfo>
     */
    public List<GpSqlInfo> importGPInfo(MultipartFile file, String schema, Integer index, String belongTo) throws Exception {
        List<GpSqlInfo> sqlInfos = getTableInfo(file, schema, index, belongTo);
        if (sqlInfos != null && sqlInfos.size() > 0) {
            this.saveBatch(sqlInfos);
        }
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
    private List<GpSqlInfo> getTableInfo(MultipartFile file, String schema, Integer index, String belongTo) throws Exception {
        ExcelImportResult<GpSqlInfoImportBO> result;
        int sheets = Integer.MAX_VALUE;
        if (index == null || index < 0) {
            index = 0;
        }
        //物理删除之前创建的数据
        QueryWrapper<GpSqlInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_to", belongTo);
        this.baseMapper.delete(queryWrapper);
        List<GpSqlInfo> res = new ArrayList();
        while (index < sheets) {
            System.out.println("index:" + index);
            ImportParams importParams = new ImportParams();
            //设置导入位置
            importParams.setHeadRows(1);
            importParams.setTitleRows(0);
            importParams.setStartRows(0);
            importParams.setStartSheetIndex(index);
            result = ExcelImportUtil.importExcelMore(file.getInputStream(), GpSqlInfoImportBO.class, importParams);
            sheets =  result.getWorkbook().getNumberOfSheets();
            String sheetName = result.getWorkbook().getSheetName(index);
            System.out.println("sheetName:" + sheetName);
            if (result != null && result.getList() != null && result.getList().size() > 0) {
                List<GpSqlInfoImportBO> list = result.getList();
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    GpSqlInfoImportBO gpSqlInfoImportBO = list.get(i);
                    if (gpSqlInfoImportBO.getColumn() == null || "字段名".equals(gpSqlInfoImportBO.getColumn())) {
                        continue;
                    }
                    if (gpSqlInfoImportBO.getColumn().startsWith("dws_")) {
                        GpSqlInfo gpSqlInfo = new GpSqlInfo();
                        gpSqlInfo.setBelongTo(belongTo);
                        String tableName = gpSqlInfoImportBO.getColumn().substring(0, (gpSqlInfoImportBO.getColumn().indexOf("(") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("(")
                                : (gpSqlInfoImportBO.getColumn().indexOf("（")) != -1 ? gpSqlInfoImportBO.getColumn().indexOf("（") : gpSqlInfoImportBO.getColumn().length()));
                        gpSqlInfo.setTableName(tableName);
                        gpSqlInfo.setColumnList(new ArrayList<>());
                        while(i + 1 < size && list.get(i + 1).getColumn() != null && !list.get(i + 1).getColumn().startsWith("dws_")) {
                            if (!"字段名".equals(list.get(i + 1).getColumn())) {
                                gpSqlInfo.getColumnList().add(list.get(i + 1));
                            }
                            i++;
                        }
                        gpSqlInfo.setTableSql(gpSqlService.setGreenplumSql(gpSqlInfo.getColumnList(), tableName,
                                gpSqlInfoImportBO.getColumn().substring((gpSqlInfoImportBO.getColumn().indexOf("(") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("(") :
                                        (gpSqlInfoImportBO.getColumn().indexOf("（") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("（") : -1))  + 1, gpSqlInfoImportBO.getColumn().length() - 1), schema));
                        gpSqlInfo.setTableSqlMc(gpSqlService.setMaxComputeSql(gpSqlInfo.getColumnList(), tableName,
                                gpSqlInfoImportBO.getColumn().substring((gpSqlInfoImportBO.getColumn().indexOf("(") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("(") :
                                        (gpSqlInfoImportBO.getColumn().indexOf("（") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("（") : -1))  + 1, gpSqlInfoImportBO.getColumn().length() - 1)));
                        gpSqlInfo.setSort(sheetName);
                        res.add(gpSqlInfo);
                    }

                }
            }
            index++;
        }
        return res;
    }
}
