package com.alex.springcloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper {

    int decrease(@Param("userId") Long userId, @Param("money")BigDecimal money);
}
