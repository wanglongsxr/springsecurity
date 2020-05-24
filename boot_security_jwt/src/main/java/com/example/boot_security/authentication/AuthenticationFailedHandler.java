package com.example.boot_security.authentication;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 自定义鉴定失败处理器
 * 鉴定失败后不跳转登录页面
 * 而直接将错误提示的JSON返回
 * @author: wanglong
 * @time: 2020/3/21 9:40
 */
@Component
@Slf4j
public class AuthenticationFailedHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        System.err.println("登录失败~~~~~~~");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setCharacterEncoding("UTF-8");
        JSON.writeJSONString(response.getWriter(),exception.getMessage());
    }
}
