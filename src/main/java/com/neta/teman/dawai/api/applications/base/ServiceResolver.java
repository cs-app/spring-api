package com.neta.teman.dawai.api.applications.base;

/**
 * this class return from login service in controller</br>
 * Recommended value</br>
 * 0 = success</br>
 * 1 = invalid value or data not valid for next statement</br>
 * 2 = negative case</br>
 * >= 3 define by your logic for negative case </br>
 */
public class ServiceResolver<T> {

    private int code = 0;

    private String message;

    private T result;

    public ServiceResolver() {
    }

    public ServiceResolver(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceResolver(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setCodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return 0 == code;
    }

    public boolean isError() {
        return 0 != code;
    }

}
