package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.User;

public interface UserServices {

    User findByUsernameAndPassword(String username, String password);

    ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password);

}
