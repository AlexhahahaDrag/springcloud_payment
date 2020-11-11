package com.alex.springcloud.controller;

import com.alex.springcloud.service.SqlInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public boolean importItemDetail(@ApiParam(value = "type", required = true) @RequestParam(value = "type") String type,
                                    @ApiParam(value = "file", required = true) @RequestParam(name="file") MultipartFile file,
                                    @ApiParam(value = "startSheet") @RequestParam(name="startSheet", required = false) Integer startSheet) throws Exception {
        return sqlInfoService.importInfo(type, file, startSheet);
    }
}
