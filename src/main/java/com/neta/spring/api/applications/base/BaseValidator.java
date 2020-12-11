package com.neta.spring.api.applications.base;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class BaseValidator {


    /**
     * new entities id, 0 = is new
     */
    protected boolean isNew(int id) {
        return id == 0;
    }

    /**
     * new entities id, 0 = is new
     */
    protected boolean isNew(long id) {
        return id == 0;
    }

    protected boolean isNull(String value) {
        return value == null;
    }

    protected boolean isNull(String[] value) {
        return (value == null || value.length == 0);
    }

    protected boolean isNull(MultipartFile value) {
        return value == null;
    }

    protected boolean isNull(BaseEntity value) {
        return value == null;
    }

    protected boolean isNullOrEmpty(Object value) {
        return null == value;
    }

    protected boolean isNullOrEmpty(String value) {
        if (value == null) {
            return true;
        } else {
            return value.trim().isEmpty();
        }

    }

    protected boolean isMinLength(String value, int length) {
        return (Objects.nonNull(value) && value.trim().toCharArray().length >= length);
    }

    protected boolean isMaxLength(String value, int length) {
        return (Objects.nonNull(value) && value.trim().toCharArray().length <= length);
    }

    protected boolean isMinAndMaxLength(String value, int minLength, int maxLength) {
        return isMinLength(value, minLength) && isMaxLength(value, maxLength);
    }

}
