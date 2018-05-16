package com.dry3.controller.backend;import com.dry3.common.Const;import com.dry3.common.ServerResponse;import com.dry3.pojo.User;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.RequestMapping;import com.dry3.service.IUserService;import org.springframework.web.bind.annotation.RequestMethod;import org.springframework.web.bind.annotation.RequestParam;import org.springframework.web.bind.annotation.ResponseBody;import javax.servlet.http.HttpSession;/** * Created by dry3 */@Controller@RequestMapping("/manage/user")public class UserManageController {    @Autowired    private IUserService iUserService;    @RequestMapping(value = "login.do", method = RequestMethod.POST)    @ResponseBody    public ServerResponse login(@RequestParam("username")  String username, @RequestParam("password")String password, HttpSession session) {        ServerResponse<User> loginResponse = iUserService.login(username, password);        if (loginResponse.isSuccess()) {            User user = loginResponse.getData();            if (user.getRole() == Const.Role.ROLE_ADMIN) {                //说明登录的是管理员                session.setAttribute(Const.CURRENT_USER, user);                return loginResponse;            } else {                return ServerResponse.createByErrorMessage("账号权限不够");            }        }        return loginResponse;    }}