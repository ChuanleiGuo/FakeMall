package com.fakemall.controller.backend;

import com.fakemall.common.Const;
import com.fakemall.common.ServerResponse;
import com.fakemall.model.User;
import com.fakemall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = userService.login(username,password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("not admin");
            }
        }
        return response;
    }

}
