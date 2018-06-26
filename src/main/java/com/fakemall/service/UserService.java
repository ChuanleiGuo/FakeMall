package com.fakemall.service;

import com.fakemall.common.ServerResponse;
import com.fakemall.model.User;

public interface UserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken);

    ServerResponse<String> resetPassword(User user, String oldPassword, String newPassword);

    ServerResponse<String> updateInformation(User user);

    ServerResponse<String> getInfomation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);
}
