package com.neta.teman.dawai.api.applications.commons;

import java.util.Objects;

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
