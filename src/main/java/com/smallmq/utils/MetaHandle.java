package com.smallmq.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


@Component
@Slf4j
public class MetaHandle implements MetaObjectHandler {
    @Autowired
    private HttpSession session;

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insertFill");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", session.getAttribute("employee") != null?session.getAttribute("employee"):session.getAttribute("user"));
        metaObject.setValue("updateUser", session.getAttribute("employee") != null?session.getAttribute("employee"):session.getAttribute("user"));

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("updateFill");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", session.getAttribute("employee")!= null?session.getAttribute("employee"):session.getAttribute("user"));


    }
}

