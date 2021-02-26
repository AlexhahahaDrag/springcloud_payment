package com.alex.springcloud.service.impl;

import com.alex.springcloud.mapper.AccountMapper;
import com.alex.springcloud.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public int decrease(Long userId, BigDecimal money) {
        try { TimeUnit.SECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        accountMapper.decrease(userId, money);
        return 1;
    }
}
