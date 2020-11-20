package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.payload.response.DUKFIlterResponse;

public interface DUKService {
    ServiceResolver<DUKFIlterResponse> filterParams();
}
