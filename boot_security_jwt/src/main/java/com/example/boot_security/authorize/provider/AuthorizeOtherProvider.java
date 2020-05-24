package com.example.boot_security.authorize.provider;

import com.example.boot_security.authentication.JwtAuthenticationTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @description: security相关配置，完全摒弃session以及添加jwt校验
 * @author: wanglong
 * @time: 2020/5/17 11:31
 */
@Component
@Order(Byte.MIN_VALUE + 2)
@AllArgsConstructor
public class AuthorizeOtherProvider implements AuthorizeProvider {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Override
    public void config(HttpSecurity httpSecurity) throws Exception {
        /*
         * 基于token，所以不需要session
         * 然后来吧我们的jwt过滤器加到 登陆登陆验证器的前面
         */
        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
