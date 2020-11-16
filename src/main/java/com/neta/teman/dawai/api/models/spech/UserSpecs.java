package com.neta.teman.dawai.api.models.spech;

import com.neta.teman.dawai.api.models.dao.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Objects;

public class UserSpecs {

    public static Specification<User> name(String name) {
        return (root, query, criteriaBuilder) -> {
            StringBuilder key = new StringBuilder();
            key.append("%").append(Objects.isNull(name) ? "" : name).append("%");
            String searchKey = key.toString();
            Join<User, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
            Join<User, Role> roleJoin = root.join("role", JoinType.LEFT);
            Predicate emnip = criteriaBuilder.like(employeeJoin.get("nip"), searchKey);
            Predicate empNama = criteriaBuilder.like(employeeJoin.get("nama"), searchKey);
            Predicate rolePredicate = criteriaBuilder.like(roleJoin.get("name"), searchKey);
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.or(emnip, empNama, rolePredicate);
        };
    }

    public static Specification<User> page(String name) {
        return (root, query, criteriaBuilder) -> {
            StringBuilder key = new StringBuilder();
            key.append("%").append(Objects.isNull(name) ? "" : name).append("%");
            String searchKey = key.toString();
            Join<User, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
            Join<User, Role> roleJoin = root.join("role", JoinType.LEFT);
            Predicate emnip = criteriaBuilder.like(employeeJoin.get("nip"), searchKey);
            Predicate empNama = criteriaBuilder.like(employeeJoin.get("nama"), searchKey);
            Predicate rolePredicate = criteriaBuilder.like(roleJoin.get("name"), searchKey);
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return empNama;
        };
    }

    public static Specification<User> golongan(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
            Join<Employee, EmployeePangkatHis> pangkatHisJoin = employeeJoin.join("pangkats", JoinType.LEFT);
            Join<EmployeePangkatHis, PangkatGolongan> golonganJoin = pangkatHisJoin.join("pangkatGolongan", JoinType.LEFT);
            Predicate golongan = criteriaBuilder.equal(golonganJoin.get("golongan"), name);
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.and(golongan);
        };
    }

    public static Specification<User> nip(String nip) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
            return criteriaBuilder.equal(employeeJoin.get("nip"), nip);
        };
    }

    public static Specification<User> login(String nip, String password) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Employee> employeeJoin = root.join("employee", JoinType.LEFT);
            Predicate userNip = criteriaBuilder.like(employeeJoin.get("nip"), nip);
            Predicate userPas = criteriaBuilder.equal(root.get("password"), DigestUtils.md5Hex(password));
            return criteriaBuilder.and(userNip, userPas);
        };
    }


}
