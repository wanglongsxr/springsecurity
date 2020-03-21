package com.example.boot_securtiy.authentication;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 自定义鉴定成功处理器
 * 鉴定成功后不跳转首页
 * 而直接将成功提示的JSON返回
 * @author: wanglong
 * @time: 2020/3/21 9:55
 */
@Component
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.err.println("登录成功~~~~~~~~~~~~~~~~~~~··");
        response.setStatus(HttpStatus.OK.value());
        JSON.writeJSONString(response.getWriter(),authentication);
    }
}
