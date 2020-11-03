package com.neta.teman.dawai.api.models.payload.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.ResponseConverter;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.payload.response.CutiResponse;
import com.neta.teman.dawai.api.models.payload.response.HolidayResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HolidayConverter implements ResponseConverter<List<HolidayResponse>, List<Holiday>> {

    @Override
    public List<HolidayResponse> convert(List<Holiday> param) {
        return BeanCopy.copyCollection(param, HolidayResponse.class);
    }
}
