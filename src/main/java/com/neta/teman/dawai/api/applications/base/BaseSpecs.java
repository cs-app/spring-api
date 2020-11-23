package com.neta.teman.dawai.api.applications.base;

import java.util.Objects;

public class BaseSpecs {

    protected static String paramLike(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    protected static String paramStartWith(String value) {
        StringBuilder sb = new StringBuilder();
        // sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
        sb.append("%");
        return sb.toString();
    }

    protected static String paramEndWith(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("%");
        sb.append(Objects.isNull(value) ? "" : value);
//        sb.append("%");
        return sb.toString();
    }

    protected static boolean isGTZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value > 0;
    }

    protected static boolean isGTEqZero(Integer value) {
        if (Objects.isNull(value)) return false;
        return value >= 0;
    }

    protected static boolean isGTZero(Long value) {
        if (Objects.isNull(value)) return false;
        return value > 0;
    }
}
