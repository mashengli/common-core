package com.qiandai.pay.common.client.exception;

/**
 * Created by mashengli on 2016/8/2.
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = -5751202405386637020L;
    private int errorCode;

    public ServiceException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
