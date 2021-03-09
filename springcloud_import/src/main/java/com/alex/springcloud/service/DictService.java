package com.alex.springcloud.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import com.alex.springcloud.entity.DictImportInfo;
import com.alex.springcloud.entity.DictInfoBo;
import com.alex.springcloud.entity.GpSqlInfo;
import com.alex.springcloud.mapper.DictInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 字典服务
 * @author:      alex
 * @createTime:  2021/3/5 9:30
 * @version:     1.0
 */
@Service
@Slf4j
public class DictService extends ServiceImpl<DictInfoMapper, DictInfoBo> {

    /**
     * @param file         字典数据
     * @param startSheet   开始sheet页
     * @param endSheet     结束sheet页
     * @param sysCode      系统编码
     * @param belongTo     归属
     * @param dictTable    字典表名
     * @description:       保存字典表插入信息
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.DictInfoBo>
     */
    public List<DictInfoBo> dictInfo(MultipartFile file, Integer startSheet, Integer endSheet, String sysCode, String belongTo, String dictTable, Integer id) throws Exception {
        //物理删除之前创建的数据
        QueryWrapper<DictInfoBo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_to", belongTo);
        queryWrapper.eq("sys_code", sysCode);
        this.baseMapper.delete(queryWrapper);
        List<DictImportInfo> sqlInfos = getDictInfo(file, startSheet, endSheet, sysCode, belongTo, dictTable);
        id = (id == null ? 1 : id);
        List<DictInfoBo> res = new ArrayList<>();
        if (sqlInfos != null && sqlInfos.size() > 0) {
            DictInfoBo dictInfoBo = new DictInfoBo();
            dictInfoBo.setBelongTo(belongTo);
            dictInfoBo.setId(id);
            dictInfoBo.setSysCode(sysCode);
            StringBuilder sb = new StringBuilder();
            for (DictImportInfo dictImportInfo : sqlInfos) {
                sb.append("insert into " + sysCode + "." + dictTable + "(id, clazz_name, clazz_code, dict_name, dict_code, description, " +
                        "operate_time, s_sdt, s_org_code, s_schema) values ("
                        + id++
                        + dictImportInfo.getClazzName() + "," + dictImportInfo.getClazzCode() + "," + dictImportInfo.getDictName() + ","
                        + dictImportInfo.getDictCode() + "," + dictImportInfo.getDescription() + ", getDate()" + ", getDate()" + ",s_org_code" + ",s_schema" + ")");
            }
            dictInfoBo.setCode(sb.toString());
            res.add(dictInfoBo);
        }
        if (res.size() > 0) {
            this.saveBatch(res);
        }
        return res;
    }

    /**
     * @param file
     * @param startSheet
     * @param endSheet
     * @param sysCode
     * @param belongTo
     * @param dictTable
     * @description:    获取字典信息
     * @author: alex
     * @return: java.util.List<com.alex.springcloud.entity.DictInfoBo>
     */
    private List<DictImportInfo> getDictInfo(MultipartFile file, Integer startSheet, Integer endSheet, String sysCode, String belongTo, String dictTable) throws Exception {
        ExcelImportResult<DictImportInfo> result;
        int sheets = Integer.MAX_VALUE;

        List<DictImportInfo> res = new ArrayList();
        Integer index = startSheet;
        while (index < sheets && index <= endSheet) {
            ImportParams importParams = new ImportParams();
            //设置导入位置
            importParams.setHeadRows(1);
            importParams.setTitleRows(0);
            importParams.setStartRows(0);
            importParams.setStartSheetIndex(index);
            result = ExcelImportUtil.importExcelMore(file.getInputStream(), DictImportInfo.class, importParams);
            sheets =  result.getWorkbook().getNumberOfSheets();
//            if (result != null && result.getList() != null && result.getList().size() > 0) {
//                List<DictImportInfo> list = result.getList();
//                int size = list.size();
//                for (int i = 0; i < size; i++) {
//                    DictImportInfo dictImportInfo = list.get(i);
//                    if (gpSqlInfoImportBO.getColumn() == null || "字段名".equals(gpSqlInfoImportBO.getColumn())) {
//                        continue;
//                    }
//                    if (gpSqlInfoImportBO.getColumn().startsWith("dws_")) {
//                        GpSqlInfo gpSqlInfo = new GpSqlInfo();
//                        gpSqlInfo.setBelongTo(belongTo);
//                        String tableName = gpSqlInfoImportBO.getColumn().substring(0, (gpSqlInfoImportBO.getColumn().indexOf("(") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("(")
//                                : (gpSqlInfoImportBO.getColumn().indexOf("（")) != -1 ? gpSqlInfoImportBO.getColumn().indexOf("（") : gpSqlInfoImportBO.getColumn().length()));
//                        gpSqlInfo.setTableName(tableName);
//                        gpSqlInfo.setColumnList(new ArrayList<>());
//                        while(i + 1 < size && list.get(i + 1).getColumn() != null && !list.get(i + 1).getColumn().startsWith("dws_")) {
//                            if (!"字段名".equals(list.get(i + 1).getColumn())) {
//                                gpSqlInfo.getColumnList().add(list.get(i + 1));
//                            }
//                            i++;
//                        }
//                        gpSqlInfo.setTableSql(gpSqlService.setGreenplumSql(gpSqlInfo.getColumnList(), tableName,
//                                gpSqlInfoImportBO.getColumn().substring((gpSqlInfoImportBO.getColumn().indexOf("(") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("(") :
//                                        (gpSqlInfoImportBO.getColumn().indexOf("（") != -1 ? gpSqlInfoImportBO.getColumn().indexOf("（") : -1))  + 1, gpSqlInfoImportBO.getColumn().length() - 1), schema));
//                        res.add(gpSqlInfo);
//                    }
//
//                }
//            }
//            index++;
        }
        return res;
    }
}
