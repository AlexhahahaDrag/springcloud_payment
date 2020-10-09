package com.alex.springcloud.service;

import com.alex.springcloud.domain.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "springcloud-storage-service")
public interface StorageService {

    @PostMapping(value = "/storage/decrease")
    CommonResult<Integer> decrease(@RequestParam("productId")Long productId, @RequestParam("count") Integer count);
}
