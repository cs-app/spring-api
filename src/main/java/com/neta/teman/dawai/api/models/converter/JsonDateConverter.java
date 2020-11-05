package com.neta.teman.dawai.api.models.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateConverter extends StdConverter<String, Date> {

    private final SimpleDateFormat[] formats = {
            new SimpleDateFormat("dd-MMM-yyyy"),
            new SimpleDateFormat("dd MMMM yyyy"),
            new SimpleDateFormat("yyyy-MM-dd")
    };

    @Override
    public Date convert(final String value) {
        if (value == null || value.equals("NULL")) {
            return null;
        }
        for (SimpleDateFormat s : formats) {
            try {
                return s.parse(value);
            } catch (ParseException ignore) {
            }
        }
        // try to trim 24 June 2035 ( 14 Tahun 07 Bulan )
        if (value.length() < 12) return null;
        String trimDate = value.substring(0, 12).trim();
        for (SimpleDateFormat s : formats) {
            try {
                return s.parse(trimDate);
            } catch (ParseException ignore) {
            }
        }
        return null;
    }

}
