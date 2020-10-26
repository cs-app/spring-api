package com.neta.teman.dawai.api.models.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateConverter extends StdConverter<String, Date> {

    private final SimpleDateFormat[] formats = {
            new SimpleDateFormat("dd-MMM-yyyy")
    };

    @Override
    public Date convert(final String value) {
        if (value == null || value.equals("NULL")) {
            return null;
        }
        for (SimpleDateFormat s : formats) {
            try {
                return s.parse(value);
            } catch (ParseException e) {
//                throw new IllegalStateException("Unable to parse date", e);
            }
        }
        return null;
    }

}
