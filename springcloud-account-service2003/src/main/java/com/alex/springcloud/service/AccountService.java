package com.alex.springcloud.service;

import java.math.BigDecimal;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
public interface AccountService {
    
    /**
     * @param userId
     * @param money
     * @description:
     * @author: alex
     * @return: int
     */
    int decrease(Long userId, BigDecimal money);
}
