package com.example.boot_securtiy.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Optional;

/**
 * @description:
 * @author: wanglong
 * @time: 2020/3/17 14:10
 */
@Controller
public class LoginController {

    /**
     * @description 自定义登录页
     * @return java.lang.String
     * @author wanglong
     * @date 2020/3/20 21:24
     */
    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    /**
     * @description 登录成功之后的跳转页
     * @return java.lang.String
     * @author wanglong
     * @date 2020/3/20 21:24
     */
    @RequestMapping("/login-success")
    public String loginSuccess(){
        return "hello";
    }

    //r1资源
    @GetMapping("/r/r1")
    @ResponseBody
    public String r1(){
        return getUserName() + "访问r1资源";
    }

    //r2资源
    @GetMapping("/r/r2")
    @ResponseBody
    public String r2(){
        return getUserName() + "访问r2资源";
    }


    /**
     * @description 获取当前登录人名称
     * @return java.lang.String
     * @author wanglong
     * @date 2020/3/20 21:25
     */
    public String getUserName(){
        //security完成认证之后，会通过sessionAuthenticaion将用户信息存储到SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取用户信息
        Object principal = authentication.getPrincipal();
        //如果为空，则为匿名，不为空，返回
        String userName = Optional.ofNullable(principal).map(name -> {
            if (name instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) name;
                return userDetails.getUsername();
            } else {
                return name.toString();
            }
        }).orElse("匿名");
        return userName;
    }
}
