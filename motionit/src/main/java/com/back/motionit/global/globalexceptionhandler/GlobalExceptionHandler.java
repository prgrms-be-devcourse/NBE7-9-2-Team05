package com.back.motionit.global.globalexceptionhandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.back.motionit.global.exception.ServiceException;
import com.back.motionit.global.respoonsedata.ResponseData;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public ResponseData<Void> handleException(ServiceException ex) {
		return new ResponseData<Void>(
			ex.getResultCode(),
			ex.getMsg()
		);
	}
}
