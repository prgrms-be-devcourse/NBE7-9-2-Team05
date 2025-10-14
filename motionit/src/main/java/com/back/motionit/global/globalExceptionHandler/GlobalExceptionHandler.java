package com.back.motionit.global.globalExceptionHandler;

import com.back.motionit.global.exception.ServiceException;
import com.back.motionit.global.respoonseData.ResponseData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseData<Void> handleException(ServiceException e) {
        return new ResponseData<Void>(
                e.getResultCode(),
                e.getMsg()
        );
    }
}
