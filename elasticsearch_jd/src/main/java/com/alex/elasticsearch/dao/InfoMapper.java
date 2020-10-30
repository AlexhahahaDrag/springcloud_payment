package com.alex.elasticsearch.dao;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface InfoMapper{

    void insertInfo(@Param("name") String name, @Param("description") String description,
                    @Param("defaultv") String defaultv, @Param("type") String type, @Param("keyword") String keyword);
}
