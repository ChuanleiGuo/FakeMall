package com.fakemall.service;

import com.fakemall.common.ServerResponse;
import com.fakemall.model.User;

public interface UserService {

    ServerResponse<User> login(String username, String password);

}
