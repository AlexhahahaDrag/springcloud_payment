package com.alex.elasticsearch.dao;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface InfoMapper{

    /**
     * @param name
     * @param description
     * @param defaultv
     * @param type
     * @param keyword
     * @description:
     * @author: alex
     * @return: void
     */
    void insertInfo(@Param("name") String name, @Param("description") String description,
                    @Param("defaultv") String defaultv, @Param("type") String type, @Param("keyword") String keyword);
}
