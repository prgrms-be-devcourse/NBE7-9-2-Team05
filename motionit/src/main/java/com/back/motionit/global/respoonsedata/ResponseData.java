package com.back.motionit.global.respoonsedata;

import com.back.motionit.global.error.code.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseData<T> {

	private String resultCode;
	private String msg;
	private T data;

	public ResponseData(String resultCode, String msg) {
		this.resultCode = resultCode;
		this.msg = msg;
		this.data = null;
	}

	public static <T> ResponseData<T> success(String code, String message, T data) {
		return new ResponseData<>(code, message, data);
	}

	public static <T> ResponseData<T> success(String message, T data) {
		return new ResponseData<>("200", message, data);
	}

	public static <T> ResponseData<T> success(T data) {
		return new ResponseData<>("200", "정상적으로 처리되었습니다.", data);
	}

	public static <T> ResponseData<T> error(ErrorCode errorCode) {
		return new ResponseData<>(errorCode.getCode(), errorCode.getMessage());
	}

	@JsonIgnore
	public int getStatusCode() {
		String statusCode = resultCode.split("-")[1];
		return Integer.parseInt(statusCode);
	}
}
