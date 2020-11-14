package com.neta.teman.dawai.api.models.spech;

import com.neta.teman.dawai.api.models.dao.Document;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.Objects;

public class DocumentSpecs {

    public static Specification<Document> name(String filter) {
        return (root, query, criteriaBuilder) -> {
            StringBuilder key = new StringBuilder();
            key.append("%").append(Objects.isNull(filter) ? "" : filter).append("%");
            String searchKey = key.toString();
            Predicate filterName = criteriaBuilder.like(root.get("name"), searchKey);
            return criteriaBuilder.and(filterName);
        };
    }

}
