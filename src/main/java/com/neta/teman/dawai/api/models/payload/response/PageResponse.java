package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PageResponse<T> {

    public PageResponse(ServiceResolver<Page<T>> resolver) {
        Page<T> page = resolver.getResult();
        if (Objects.isNull(page)) return;
        this.values = page.getContent();
        this.pageTotal = page.getTotalPages();
        this.elementTotal = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isEmpty = page.isEmpty();
        this.isLast = page.isLast();
    }

    public PageResponse(Page<T> page) {
        if (Objects.isNull(page)) return;
        this.values = page.getContent();
        this.pageTotal = page.getTotalPages();
        this.elementTotal = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isEmpty = page.isEmpty();
        this.isLast = page.isLast();
    }

    List<T> values;

    long elementTotal;

    int page;

    int pageTotal;

    boolean isFirst;

    boolean isEmpty;

    boolean isLast;

}
