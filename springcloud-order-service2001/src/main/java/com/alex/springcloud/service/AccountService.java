package com.alex.springcloud.service;

import com.alex.springcloud.domain.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@FeignClient(value = "springcloud-account-service")
public interface AccountService {

    /**
     * @param userId
     * @param money
     * @description:
     * @author: alex
     * @return: com.alex.springcloud.domain.CommonResult<java.lang.Integer>
     */
    @PostMapping(value = "/account/decrease")
    CommonResult<Integer> decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
}
