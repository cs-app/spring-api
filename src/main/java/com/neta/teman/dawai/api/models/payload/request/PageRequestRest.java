package com.neta.teman.dawai.api.models.payload.request;

import com.neta.teman.dawai.api.applications.constants.AppConstants;
import lombok.Data;
import org.springframework.data.domain.PageRequest;

@Data
public class PageRequestRest {

    int page;

    public PageRequest pageRequest() {
        return PageRequest.of(page, AppConstants.Page.SHOW_IN_PAGE);
    }
}
