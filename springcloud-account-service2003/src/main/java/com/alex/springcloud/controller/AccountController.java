package com.alex.springcloud.controller;

import com.alex.springcloud.domain.CommonResult;
import com.alex.springcloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/10/9 14:35
 * @version:     1.0
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/decrease")
    public CommonResult<Integer> decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money) {
        int result = accountService.decrease(userId, money);
        return new CommonResult<>(200, "扣减余额成功", result);
    }
}
