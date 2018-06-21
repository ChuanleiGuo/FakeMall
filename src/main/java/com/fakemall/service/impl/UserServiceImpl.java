package com.fakemall.service.impl;

import com.fakemall.common.ServerResponse;
import com.fakemall.dao.UserMapper;
import com.fakemall.model.User;
import com.fakemall.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resuleCount = userMapper.checkUsername(username);
        if (resuleCount == 0) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }

        //TODO: MD5
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("wrong password");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("success", user);
    }
}
