package com.neta.teman.dawai.api.applications.base;

import com.neta.teman.dawai.api.applications.commons.ValueValidation;

import java.util.Objects;

@SuppressWarnings({"rawtypes"})
public abstract class BaseService {

    protected boolean isNull(Object... value) {
        return ValueValidation.isNull(value);
    }

    protected boolean nonNull(Object... value) {
        return !ValueValidation.isNull(value);
    }

    protected String paramLike(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    protected ServiceResolver success() {
        return success(0, "success");
    }

    protected ServiceResolver success(String message) {
        return success(0, message);
    }

    protected <T> ServiceResolver<T> success(T result) {
        return success(0, "success", result);
    }

    protected <T> ServiceResolver success(int code, String message) {
        return new ServiceResolver(code, message);
    }

    protected <T> ServiceResolver<T> success(int code, String message, T result) {
        return new ServiceResolver<T>(code, message, result);
    }

    protected ServiceResolver error() {
        return error(1, "failed");
    }

    protected ServiceResolver error(String message) {
        return error(1, message);
    }

    protected ServiceResolver error(int code) {
        return error(code, "failed");
    }

    protected <T> ServiceResolver<T> error(int code, String message) {
        return new ServiceResolver<T>(code, message);
    }

    protected <T> ServiceResolver<T> error(int code, String message, T result) {
        return new ServiceResolver<T>(code, message, result);
    }

}
