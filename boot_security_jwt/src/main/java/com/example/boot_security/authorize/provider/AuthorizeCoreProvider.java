package com.example.boot_security.authorize.provider;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * @description: security相关配置 配置jwt放行url
 * @author: wanglong
 * @time: 2020/5/17 11:29
 */
@Component
@Order(Byte.MIN_VALUE + 1)
public class AuthorizeCoreProvider implements AuthorizeProvider {

    @Override
    public void config(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers(getPermitUrls())
                .permitAll();
    }

    public String[] getPermitUrls() {
        return new String[]{"/login","/authentication"};
    }
}
