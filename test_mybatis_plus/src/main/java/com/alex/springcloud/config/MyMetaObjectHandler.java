package com.alex.springcloud.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 填充器
 *
 * @author nieqiurong 2018-08-10 22:59:23.
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createDate",Date.class, new Date());
        this.strictInsertFill(metaObject, "updateDate",Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictInsertFill(metaObject, "updateDate",Date.class, new Date());
    }
}