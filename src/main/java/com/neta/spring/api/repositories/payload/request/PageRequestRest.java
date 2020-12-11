package com.neta.spring.api.repositories.payload.request;

import org.springframework.data.domain.PageRequest;
import lombok.Data;

@Data
public class PageRequestRest {

    int page;

    public PageRequest pageRequest() {
        return PageRequest.of(page, 10);
    }
}
