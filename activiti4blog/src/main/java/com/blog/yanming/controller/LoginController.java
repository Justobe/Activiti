package com.blog.yanming.controller;

/**
 * Created by YanMing on 2017/7/13.
 */

import com.blog.yanming.entity.User;
import com.blog.yanming.service.UserService;
import com.blog.yanming.utils.SpringSessionUtil;
import com.sun.org.apache.bcel.internal.generic.AALOAD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

/**
 * @Date: 13:41 2017/6/5
 * 控制登陆、注册、主页跳转功能
 *
 */
@Controller
@RequestMapping("/")
public class LoginController {

    /**
     * @Date: 13:18 2017/6/6
     * 控制"/login"、"/"跳转，检测session中是否登录
     * 如果登录直接跳转达到主页index.html
     */
    @Autowired
    private UserService userService;


    /*@Autowired
    private IdentityService identityService;
    */
    @RequestMapping(value = {"/login","/"}, method = RequestMethod.GET)
    public String login(HttpSession session) {
        User tmpUser = SpringSessionUtil.getSession(session);
        if (tmpUser != null) {
            return "redirect:/loginOK";
        } else {
            return "loginPage";
        }

    }
    /**
     * @Date: 13:21 2017/6/6
     * 跳转到注册页面
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {

        return "registerPage";
    }


    /*@RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String registerPost(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("group") String group) {

        User user = userService.getUserByUsername(username);
        if (user == null){
            User newUser = new User(username, password);
            userService.saveNewUser(newUser);
            org.activiti.engine.identity.User act_user = identityService.newUser(username);
            act_user.setId(username);
            act_user.setFirstName(username);
            act_user.setPassword(password);

            identityService.saveUser(act_user);
            identityService.createMembership(username, group);
            return "register_success";
        }

        return "user_has_existed";
    }
*/

    /**
     * @Date: 13:22 2017/6/6
     * 登录功能返回登录状态码
     * right/wrong
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String userLogin(@RequestParam(value = "username") String username,
                            @RequestParam(value = "password") String password,
                            HttpSession session) {
        //检验是否session中是否已登录
        User tmpUser = userService.getUserByUsername(username);
        if (tmpUser != null && (tmpUser.getPassword().equals(password))) {
            //登录当前用户，保存到session
            SpringSessionUtil.setSession(session, tmpUser);
            return "right";
        } else {
            return "wrong";
        }
    }

    /**
     * @Date: 13:27 2017/6/6
     * 登陆成功跳转
     */
    @RequestMapping(value = "/loginOK")
    public String loginOK() {
        //return "mainPage";
        return "index";
    }

    /**
     * @Date: 13:28 2017/6/26
     * 登陆退出
     */
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(SpringSessionUtil.USER);
        return "loginPage";
    }

}