package com.example.boot_securtiy.authentication;

import com.alibaba.fastjson.JSON;
import com.example.boot_securtiy.model.AuthenticaitonDetails;
import com.example.boot_securtiy.model.LoginRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 登录过滤器
 * @author: wanglong
 * @time: 2020/3/26 16:34
 */

public class LoginAuthentication extends AbstractAuthenticationProcessingFilter {

    private static final String SECURITY_LOGIN_URL = "/authentication/form";

    public LoginAuthentication() {
        super(new AntPathRequestMatcher(SECURITY_LOGIN_URL,"POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String requestBody = IOUtils.toString(request.getReader());
        LoginRequest loginRequest = JSON.parseObject(requestBody,LoginRequest.class);
        if(loginRequest == null || loginRequest.isInvalid()){
            throw new InsufficientAuthenticationException("身份验证失败");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword());
        token.setDetails(new AuthenticaitonDetails(request,loginRequest.getCaptcha()));
        return this.getAuthenticationManager().authenticate(token);
    }
}
