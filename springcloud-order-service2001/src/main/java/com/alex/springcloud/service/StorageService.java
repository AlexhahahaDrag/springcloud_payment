package com.alex.springcloud.service;

import com.alex.springcloud.domain.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "springcloud-storage-service")
public interface StorageService {

    /**
     * @param productId
     * @param count
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.domain.CommonResult<java.lang.Integer>
     */
    @PostMapping(value = "/storage/decrease")
    CommonResult<Integer> decrease(@RequestParam("productId")Long productId, @RequestParam("count") Integer count);
}
