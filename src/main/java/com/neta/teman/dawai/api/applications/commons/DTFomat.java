package com.neta.teman.dawai.api.applications.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DTFomat {

    public static String format(Long date){
        if(Objects.isNull(date)) return "";
        return new SimpleDateFormat("dd-MMM-yyyy").format(new Date(date));
    }

    public static String format(Date date){
        if(Objects.isNull(date)) return "";
        return new SimpleDateFormat("dd-MMM-yyyy").format(date);
    }
}
