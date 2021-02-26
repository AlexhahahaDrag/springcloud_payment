package com.alex.springcloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @description:
 * @author:      alex
 * @createTime:  2021/2/26 15:01
 * @version:     1.0
 */
@Mapper
public interface AccountMapper {

    /**
     * @param userId
     * @param money
     * @description:
     * @author: alex
     * @return: int
     */
    int decrease(@Param("userId") Long userId, @Param("money")BigDecimal money);
}
