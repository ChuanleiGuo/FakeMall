package com.fakemall.service.impl;

import com.fakemall.common.ServerResponse;
import com.fakemall.common.TokenCache;
import com.fakemall.dao.UserMapper;
import com.fakemall.model.User;
import com.fakemall.service.UserService;
import com.fakemall.common.Const;
import com.fakemall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("wrong password");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("success", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("register fail");
        }
        return ServerResponse.createBySuccessMessage("register success");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isBlank(type)) {
            return ServerResponse.createByErrorMessage("wrong argument");
        }

        switch (type) {
            case Const.EMAIL:
                if (userMapper.checkEmail(str) > 0) {
                    return ServerResponse.createByErrorMessage("email exists");
                }
                break;
            case Const.USERNAME:
                if (userMapper.checkUsername(str) > 0) {
                    return ServerResponse.createByErrorMessage("user exists");
                }
                break;
            default:
                return ServerResponse.createByErrorMessage("unsupported check type");
        }

        return ServerResponse.createBySuccessMessage("check success");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }
        String question = userMapper.selectQestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("question is empty");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("wrong answer");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToekn) {
        if (StringUtils.isBlank(forgetToekn)) {
            return ServerResponse.createByErrorMessage("need token");
        }
        ServerResponse<String> validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("user does not exist");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token expired");
        }

        if (StringUtils.equals(forgetToekn, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("update password success");
            }
        } else {
            return ServerResponse.createByErrorMessage("invalid token");
        }
        return ServerResponse.createByErrorMessage("update password failed");
    }
}
