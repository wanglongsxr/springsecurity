package com.example.boot_securtiy.authentication;

import com.alibaba.fastjson.JSON;
import com.example.boot_securtiy.model.LoginRequest;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @description: 登录过滤器
 * @author: wanglong
 * @time: 2020/3/26 16:34
 */
@Component
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String SECURITY_LOGIN_URL = "/authentication/form";

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailedHandler authenticationFailedHandler;
    @Autowired
    private PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    @PostConstruct
    public void init() {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(SECURITY_LOGIN_URL, "POST"));
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailedHandler);
        setRememberMeServices(persistentTokenBasedRememberMeServices);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //校验验证码
        verificationCaptcha(request.getParameter("captcha"),request,response);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getParameter("username"),request.getParameter("password"));
        return this.getAuthenticationManager().authenticate(token);
    }

    private void verificationCaptcha(String captcha,HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String verificationCode = (String)session.getAttribute("captcha");
        if(!StringUtils.isEmpty(verificationCode)) {
            //清除验证码，不管是或成功
            session.removeAttribute("captcha");
            if (!StringUtils.isEmpty(verificationCode) && !captcha.equals(verificationCode)) {
                throw new AuthenticationServiceException("验证码错误!");
            }
        }
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
