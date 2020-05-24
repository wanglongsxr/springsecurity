package com.example.boot_security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @description: 首页
 * @author: wanglong
 * @time: 2020/3/17 14:10
 */
@Controller
public class LoginController {

    /**
     * @description 登录成功之后的跳转页
     * @return java.lang.String
     * @author wanglong
     * @date 2020/3/20 21:24
     */
    @RequestMapping("/login-success")
    @ResponseBody
    public String loginSuccess(){
        return "访问首页成功";
    }

}
