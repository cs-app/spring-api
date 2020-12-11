package com.neta.spring.api.applications.commons;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public static String format(LocalDate userpen) {
        return userpen.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

    public static String formatUntilMonth(Date date) {
        if(Objects.isNull(date)) return "";
        return new SimpleDateFormat("MMMM yyyy").format(date);
    }
}
