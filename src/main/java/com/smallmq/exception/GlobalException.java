package com.smallmq.exception;

import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
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
        return Response.error(e.getMessage());
    }
}
