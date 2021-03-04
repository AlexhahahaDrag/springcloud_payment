package com.alex.springcloud.controller;

import com.alex.springcloud.entity.GpSqlInfo;
import com.alex.springcloud.entity.SqlInfo;
import com.alex.springcloud.service.GpSqlInfoService;
import com.alex.springcloud.service.SqlInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/10 17:37
 * @version:     1.0
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping("/sql")
@Api(value = "SqlInfoController", description = "restful api")
public class SqlInfoController {

    @Autowired
    private SqlInfoService sqlInfoService;

    @Autowired
    private GpSqlInfoService gpSqlInfoService;

    @ApiOperation(value = "客户信息批量导入", httpMethod = "POST", notes = "客户信息批量导入", produces = "application/json; charset=utf-8")
    @PostMapping("/import")
    public List<SqlInfo> importItemDetail(@ApiParam(value = "ods标准表文件", required = true) @RequestBody MultipartFile file,
                                          @ApiParam(value = "dwd系统编号", defaultValue = "", example = "302") @RequestParam(name="dwdSysCode", required = false, defaultValue = "") String dwdSysCode,
                                          @ApiParam(value = "ods标准表前缀", defaultValue = "", example = "PRJ_302_DW_dev") @RequestParam(name="odsPrefix", required = false, defaultValue = "") String odsPrefix,
//                                          @ApiParam(value = "ods原始前缀", defaultValue = "", example = "PRJ_1020005_ods_prod") @RequestParam(name="oldOdsPrefix", required = false, defaultValue = "") String oldOdsPrefix,
//                                          @ApiParam(value = "ods原始数据库", defaultValue = "", example = "ods_raw_1030001_100") @RequestParam(name="oldOdsDatabase", required = false, defaultValue = "") String oldOdsDatabase,
                                          @ApiParam(value = "开始的sheet页", defaultValue = "0", example = "0") @RequestParam(name="startSheet", required = false, defaultValue = "0") Integer startSheet,
                                          @ApiParam(value = "属于", defaultValue = "0", example = "lihua") @RequestParam(name="belongTo", required = false, defaultValue = "") String belongTo) throws Exception {
        String oldOdsPrefix = null;
        String oldOdsDatabase = null;
        return sqlInfoService.importInfo(file, startSheet, dwdSysCode, odsPrefix, belongTo, oldOdsPrefix, oldOdsDatabase);
    }

    @ApiOperation(value = "创建gp建表语句", httpMethod = "POST", notes = "批量创建gp建表语句", produces = "application/json; charset=utf-8")
    @PostMapping("/createGPsql")
    public List<GpSqlInfo> importItemDetail(@ApiParam(value = "汇总表", required = true) @RequestBody MultipartFile file,
                                            @ApiParam(value = "schema名", defaultValue = "", example = "302") @RequestParam(name="schema", required = false, defaultValue = "") String schema,
                                            @ApiParam(value = "开始的sheet页", defaultValue = "0", example = "0") @RequestParam(name="startSheet", required = false, defaultValue = "0") Integer startSheet,
                                            @ApiParam(value = "属于", defaultValue = "0", example = "lihua") @RequestParam(name="belongTo", required = false, defaultValue = "") String belongTo) throws Exception {
        return gpSqlInfoService.importGPInfo(file, schema, startSheet, belongTo);
    }
}
