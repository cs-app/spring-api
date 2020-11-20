package com.neta.teman.dawai.api.applications.commons;

import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.EmployeeEducation;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@Component
public class UserCommons {

    public String lastEducation(User user) {
        if (Objects.isNull(user)) return null;
        if (Objects.isNull(user.getEmployee())) return null;
        List<EmployeeEducation> educationList = user.getEmployee().getEducations();
        if (Objects.isNull(educationList)) return null;
        Optional<EmployeeEducation> optional = educationList.stream().max(Comparator.comparing(EmployeeEducation::getGraduated));
        return optional.map(EmployeeEducation::getType).orElse(null);
    }

    public Integer getAgeYear(Date tanggalLahir) {
        LocalDate today = LocalDate.now();
        LocalDate userday = tanggalLahir.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        return diff.getYears();
    }

    public Integer getAgeMonth(Date tanggalLahir) {
        LocalDate today = LocalDate.now();
        LocalDate userday = tanggalLahir.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        return diff.getMonths();
    }

    public Integer tingkatPendidikan(String type) {
        if (Objects.isNull(type)) return null;

        return null;
    }
}
