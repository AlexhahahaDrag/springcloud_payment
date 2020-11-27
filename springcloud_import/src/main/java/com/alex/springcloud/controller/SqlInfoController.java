package com.alex.springcloud.controller;

import com.alex.springcloud.entity.SqlInfo;
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
@RestController
@RequestMapping("/sql")
@Api(value = "SqlInfoController", description = "restful api")
public class SqlInfoController {

    @Autowired
    private SqlInfoService sqlInfoService;

    @ApiOperation(value = "客户信息批量导入", httpMethod = "POST", notes = "客户信息批量导入", produces = "application/json; charset=utf-8")
    @PostMapping("/import")
    public List<SqlInfo> importItemDetail(@ApiParam(value = "ods标准表文件", required = true) @RequestBody MultipartFile file,
                                          @ApiParam(value = "dwd系统编号", defaultValue = "", example = "302") @RequestParam(name="dwdSysCode", required = false, defaultValue = "") String dwdSysCode,
                                          @ApiParam(value = "开始的sheet页", defaultValue = "0", example = "0") @RequestParam(name="startSheet", required = false, defaultValue = "0") Integer startSheet) throws Exception {
        return sqlInfoService.importInfo(file, startSheet, dwdSysCode);
    }
}
