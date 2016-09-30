package com.qiandai.pay.common.client.result;

import java.io.Serializable;

/**
 * Created by mashengli on 2016/8/2.
 */
public class BaseResult<T> implements Serializable {
    private static final long serialVersionUID = 4410832670709164787L;
    private static final int OK = 200;
    private static final String OK_MESSAGE = "OK";
    private boolean success;
    private T data;
    private int code;
    private String message;

    public BaseResult(T data) {
        this.success = true;
        this.data = data;
        this.code = OK;
        this.message = OK_MESSAGE;
    }

    public BaseResult(T data, int code, String message) {
        this.success = true;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public boolean hasData() {
        return data != null;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
