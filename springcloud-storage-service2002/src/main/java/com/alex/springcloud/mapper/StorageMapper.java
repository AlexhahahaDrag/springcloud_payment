package com.alex.springcloud.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StorageMapper {

    int decrease(@Param("productId") Long productId, @Param("count") Integer count);
}
