package com.neta.teman.dawai.api.models.payload.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FilterRequest extends PageRequestRest {

    String filter;

}
