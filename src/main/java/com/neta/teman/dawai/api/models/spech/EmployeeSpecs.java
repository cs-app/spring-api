package com.neta.teman.dawai.api.models.spech;

import com.neta.teman.dawai.api.applications.base.BaseSpecs;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.FilterDukRequest;
import com.neta.teman.dawai.api.models.repository.EmployeeEducationRepository;
import com.neta.teman.dawai.api.models.repository.JabatanRepository;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.PendidikanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EmployeeSpecs extends BaseSpecs {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    JabatanRepository jabatanRepository;

    @Autowired
    PendidikanRepository pendidikanRepository;

    @Autowired
    EmployeeEducationRepository employeeEducationRepository;

    public Specification<Employee> filter(FilterDukRequest request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();
            // name
            Predicate name = criteriaBuilder.like(root.get("nama"), paramLike(request.getName()));
            predicateList.add(name);

            // status aktif
            if (isGTZero(request.getStatus())) {
                if (1 == request.getStatus()) {
                    Predicate aktif = criteriaBuilder.equal(root.get("statusAktif"), "Aktif");
                    predicateList.add(aktif);
                } else {
                    Predicate aktif = criteriaBuilder.notEqual(root.get("statusAktif"), "Aktif");
                    predicateList.add(aktif);
                }
            }

            // pangkat
            if (isGTZero(request.getPangkat())) {
                PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById(request.getPangkat()).orElse(null);
                if (Objects.nonNull(pangkatGolongan)) {
                    Predicate gol = criteriaBuilder.equal(root.get("gol"), pangkatGolongan.getGolongan());
                    predicateList.add(gol);
                }
            }

            // jabatan
            if (isGTZero(request.getJabatan())) {
                Jabatan jabatan = jabatanRepository.findById(request.getJabatan()).orElse(null);
                if (Objects.nonNull(jabatan)) {
                    Predicate predicateJabatan = criteriaBuilder.equal(root.get("jabatan"), jabatan.getName());
                    predicateList.add(predicateJabatan);
                }
            }

            // education
            if (isGTZero(request.getPendidikan())) {
//                EmployeeEducation education = employeeEducationRepository.findById(request.getPendidikan()).orElse(null);
                Pendidikan pendidikan = pendidikanRepository.findById(request.getPendidikan()).orElse(null);
                if (Objects.nonNull(pendidikan)) {
                    Predicate predicateEducation = criteriaBuilder.equal(root.get("lastEducation"), pendidikan.getType());
                    predicateList.add(predicateEducation);
                }
            }

            // usia
            if (isGTZero(request.getUsia())) {
                Predicate usia = criteriaBuilder.equal(root.get("ageYear"), request.getUsia());
                predicateList.add(usia);
            }

            // masa kerja
            if (isGTEqZero(request.getMasaKerja())) {
                Predicate masaKerja = criteriaBuilder.equal(root.get("ageWorkYear"), request.getMasaKerja());
                predicateList.add(masaKerja);
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
