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
    public List<SqlInfo> importItemDetail(@ApiParam(value = "type", required = true) @RequestParam(value = "type") String type,
                                          @ApiParam(value = "file", required = true) @RequestBody MultipartFile file,
                                          @ApiParam(value = "startSheet", defaultValue = "0", example = "0") @RequestParam(name="startSheet", required = false, defaultValue = "0") Integer startSheet,
                                          @ApiParam(value = "isFact", required = true) @RequestParam(name="isFact") String isFact) throws Exception {
        return sqlInfoService.importInfo(type, file, startSheet, isFact);
    }
}
