package com.example.boot_securtiy.config;

import com.example.boot_securtiy.authentication.LoginAuthenticationFilter;
import com.example.boot_securtiy.service.MyUserDetailService;
import com.example.boot_securtiy.service.RememberMeTokenService;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import java.util.Properties;

/**
 * @description: Security的配置
 * @author: wanglong
 * @time: 2020/3/17 11:16
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //静态资源放行url
    private static final String[] staticDcUrl = {"/js/**"};
    //动态资源放行url
    private static final String[] dynamicDcUrl = {"/login","/captcha","/authentication/form"};

    @Autowired
    private LoginAuthenticationFilter loginAuthenticationFilter;
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
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
        PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices("remember-me"
                , myUserDetailService, rememberMeTokenService);
        services.setAlwaysRemember(true);
        return services;
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(staticDcUrl);
    }

    /**
     * @description security的安全拦截机制
     * @return void
     * @author wanglong
     * @date 2020/3/17 11:21
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(loginAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);

        http.formLogin()
                .loginPage("/login")//登录页
                .and().logout().logoutUrl("/logout")//定义退出页
                .and().authorizeRequests()
                .antMatchers("/r/r1").hasAuthority("p1")//拥有p1权限的人才能访问r1资源
                .antMatchers("/r/r2").hasAuthority("p2")//拥有p2权限的人才能访问r2资源
                .antMatchers("/r/**").authenticated()//对r/**的资源放行
                .antMatchers(dynamicDcUrl)
                .permitAll().anyRequest().authenticated()
                .and()
                .csrf().disable();

        http.authorizeRequests()
                .and()
                .rememberMe()
                .rememberMeServices(persistentTokenBasedRememberMeServices())
                .key("remember-me");
    }
}
