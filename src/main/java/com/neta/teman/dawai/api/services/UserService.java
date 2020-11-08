package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.User;

public interface UserService extends RoleService {

    ServiceResolver<User> findByNip(String nip);

    ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password);

}
