package com.back.motionit.global.respoonseData;

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

    @JsonIgnore
    public int getStatusCode() {
        String statusCode = resultCode.split("-")[0];
        return Integer.parseInt(statusCode);
    }

}