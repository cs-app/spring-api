package com.neta.teman.dawai.api.models.payload.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.payload.response.CutiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CutiConverter {

    public static List<CutiResponse> convert(List<Cuti> cutis) {
        List<CutiResponse> result = new ArrayList<>();
        for (Cuti s : cutis) {
            CutiResponse o = new CutiResponse();
            BeanCopy.copy(o, s);
            if (Objects.nonNull(s.getUser()) && Objects.nonNull(s.getUser().getEmployee())) {
                CutiResponse.CutiUserResponse user = new CutiResponse.CutiUserResponse();
                BeanCopy.copy(user, s.getUser().getEmployee());
                o.setUser(user);
            }
            if (Objects.nonNull(s.getAtasan()) && Objects.nonNull(s.getAtasan().getEmployee())) {
                CutiResponse.CutiUserResponse user = new CutiResponse.CutiUserResponse();
                BeanCopy.copy(user, s.getAtasan().getEmployee());
                o.setAtasan(user);
            }
            if (Objects.nonNull(s.getPejabat()) && Objects.nonNull(s.getPejabat().getEmployee())) {
                CutiResponse.CutiUserResponse user = new CutiResponse.CutiUserResponse();
                BeanCopy.copy(user, s.getPejabat().getEmployee());
                o.setPejabat(user);
            }
            result.add(o);
        }
        return result;
    }
}
