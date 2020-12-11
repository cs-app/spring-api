package com.neta.spring.api.applications.commons;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ValueValidation {

    /**
     * String
     */
    private static boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
    }

    /**
     * Object
     */
    private static boolean isNull(Object value) {
        if (value instanceof String) {
            return isNullOrEmpty((String) value);
        }
        return Objects.isNull(value);
    }

    public static boolean isNull(Object... values) {
        boolean result = false;
        for (Object o : values) {
            result = isNull(o);
            if (result) break;
        }
        return result;
    }

}
