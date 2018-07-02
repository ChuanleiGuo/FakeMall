package com.fakemall.controller.api;

import com.fakemall.common.Const;
import com.fakemall.common.ResponseCode;
import com.fakemall.common.ServerResponse;
import com.fakemall.model.User;
import com.fakemall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * login
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return userService.register(user);
    }

    @RequestMapping(value = "check_valid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }

    @RequestMapping(value = "forget_get_question",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return userService.selectQuestion(username);
    }


    @RequestMapping(value = "forget_check_answer",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username, String newPassword, String forgetToken) {
        return userService.forgetResetPassword(username, newPassword, forgetToken);
    }

    @RequestMapping(value = "reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("user has not loged in");
        }
        return userService.resetPassword(user, oldPassword, newPassword);
    }

    @RequestMapping(value = "update_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("user has not loged in");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = userService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "need login");
        }
        return userService.getInformation(currentUser.getId());
    }
}
