package com.example.boot_security.config;

import com.example.boot_security.authorize.mananger.AuthorizeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @description: security 配置类
 * @author: wanglong
 * @time: 2020/5/17 8:59
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 失败处理器
     */
    private final AuthenticationFailureHandler authenticationFailureHandler;
    /**
     * 成功处理器
     */
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    /**
     * 封装管理器
     */
    private final AuthorizeManager authorizeManager;

    //密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public SecurityConfig(AuthenticationFailureHandler authenticationFailureHandler, AuthenticationSuccessHandler authenticationSuccessHandler, AuthorizeManager authorizeManager) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authorizeManager = authorizeManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                // 先加上这句话，否则登录的时候会出现403错误码，Could not verify the provided CSRF token because your session was not found.
                .csrf().disable();
        // 一定要放到最后，是因为config方法里最后做了其他任何方法都需要身份认证才能访问。
        // 放到前面的话，后面在加载.antMatchers(getPermitUrls()).permitAll()的时候也会被认为无权限，
        // 因为前面已经做了其他任何方法都需要身份认证才能访问，SpringSecurity是有先后顺序的。
        authorizeManager.config(http);
    }
}
