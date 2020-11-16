package com.neta.teman.dawai.api.applications.base;

import com.fasterxml.jackson.databind.SerializationFeature;

public class Response<T> {

    private int code;

    private String message;

    private T result;

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
}
