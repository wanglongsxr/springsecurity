package com.example.boot_securtiy.config;

import com.example.boot_securtiy.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * @description: Security的配置
 * @author: wanglong
 * @time: 2020/3/17 11:16
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    //密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * @description security的安全拦截机制
     * @return void
     * @author wanglong
     * @date 2020/3/17 11:21
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//禁用csrf
                .authorizeRequests()
                .antMatchers("/r/r1").hasAuthority("p1")//拥有p1权限的人才能访问r1资源
                .antMatchers("/r/r2").hasAuthority("p2")//拥有p2权限的人才能访问r2资源
                .antMatchers("/r/**").authenticated()//对r/**的资源放行
                .antMatchers("/login", "/authentication/require",
                        "/authentication/form").permitAll()//对以上资源路径放行
                .anyRequest()// 任何请求
                .authenticated()// 都需要身份认证
                .and()
                .formLogin()//允许表单登录
                .loginPage("/login")//登录页
                .loginProcessingUrl("/authentication/form")//自定义登录
//                .successForwardUrl("/login-success")//登录成功后跳转页
                .successHandler(authenticationSuccessHandler)//自定义成功鉴权
                .failureHandler(authenticationFailureHandler)//自定义失败鉴权
                .and()
                .logout()
                .logoutUrl("/logout");
    }
}
