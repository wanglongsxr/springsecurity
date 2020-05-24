package com.example.boot_security.authorize.mananger;

import com.example.boot_security.authorize.provider.AuthorizeProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: providers管理类，采用状态者模式
 * @author: wanglong
 * @time: 2020/5/17 11:02
 */
@Component
@AllArgsConstructor
public class MyAuthorizeManager implements AuthorizeManager {

    private final List<AuthorizeProvider> providers;

    @Override
    public void config(HttpSecurity httpSecurity) throws Exception {
        for (AuthorizeProvider provider : providers) {
            provider.config(httpSecurity);
        }
    }
}
