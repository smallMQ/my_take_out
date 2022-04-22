package com.smallmq.exception;

import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalException extends Exception {
    @ExceptionHandler(value = Exception.class)
    public Response<String> GlobalException(Exception e) {
        log.error("GlobalException: {}", e.getMessage());
        e.printStackTrace();
        return Response.error(e.getMessage());
    }
    @ExceptionHandler(value = DuplicateKeyException.class)
    public Response<String> DuplicateKeyException(DuplicateKeyException e) {
        log.error("DuplicateKeyException: {}", e.getMessage());
        e.printStackTrace();
        return Response.error("用户已存在!");
    }
}
