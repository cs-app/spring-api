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
}
