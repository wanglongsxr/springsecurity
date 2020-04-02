package com.example.boot_securtiy.config;

import com.example.boot_securtiy.authentication.AuthenticationFailedHandler;
import com.example.boot_securtiy.authentication.AuthenticationSuccessHandler;
import com.example.boot_securtiy.authentication.LoginAuthentication;
import com.example.boot_securtiy.service.MyUserDetailService;
import com.example.boot_securtiy.service.RememberMeTokenService;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Properties;

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
    private AuthenticationFailedHandler authenticationFailedHandler;
    @Autowired
    private MyUserDetailService myUserDetailService;

    //具体详情请到RememberMeTokenService类查看
    @Autowired
    private RememberMeTokenService rememberMeTokenService;

    //密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginAuthentication authenticationFilter() throws Exception {
        LoginAuthentication loginAuthentication = new LoginAuthentication();
        loginAuthentication.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        loginAuthentication.setAuthenticationFailureHandler(authenticationFailedHandler);
        loginAuthentication.setAuthenticationManager(authenticationManagerBean());
        return loginAuthentication;
    }

    @Bean
    public Producer captcha() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "150");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**");
    }

    /**
     * @description security的安全拦截机制
     * @return void
     * @author wanglong
     * @date 2020/3/17 11:21
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login")//登录页
//                .loginProcessingUrl("/authentication/form")//自定义登录
//                .successHandler(authenticationSuccessHandler)//自定义成功鉴权
//                .failureHandler(authenticationFailureHandler)//自定义失败鉴权
                .and().logout().logoutUrl("/logout")//定义退出页
                .and().authorizeRequests()
                .antMatchers("/r/r1").hasAuthority("p1")//拥有p1权限的人才能访问r1资源
                .antMatchers("/r/r2").hasAuthority("p2")//拥有p2权限的人才能访问r2资源
                .antMatchers("/r/**").authenticated()//对r/**的资源放行
                .antMatchers("/login","/captcha","/authentication/form")
                .permitAll().anyRequest().authenticated()
                .and()
                .rememberMe().rememberMeParameter("rememberMe")//rememberMeParameter方法是自定义表单中的name属性，如果没有该方法，security默认强制是remember-me
                //.rememberMeCookieName("remember")//如果你想让调试菜单（F12）中的cookie换个名字的话，请使用此方法
                .tokenRepository(rememberMeTokenService)//数据访问层,token持久化方案
                .userDetailsService(myUserDetailService)//用来指定获取用户信息的服务
                .tokenValiditySeconds(3600)
                .and().addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();//token有效期，这里配置60秒
    }
}
