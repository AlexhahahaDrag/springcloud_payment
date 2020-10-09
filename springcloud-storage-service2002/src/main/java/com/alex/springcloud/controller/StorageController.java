package com.alex.springcloud.controller;

import com.alex.springcloud.domain.CommonResult;
import com.alex.springcloud.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/10/9 15:16
 * @version:     1.0
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/decrease")
    public CommonResult<Integer> decrease(@RequestParam("productId")Long productId, @RequestParam("count") Integer count) {
        int result = storageService.decrease(productId, count);
        return new CommonResult(200, "扣减库存成功", result);
    }
}
