package com.smallmq.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MetaHandle implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insertFill");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("status", 1);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("updateFill");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());

    }
}

