package com.example.boot_securtiy.authentication;

import com.example.boot_securtiy.model.AuthenticaitonDetails;
import com.example.boot_securtiy.service.MyUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @description: provider
 * @author: wanglong
 * @time: 2020/3/26 20:32
 */
@Component
public class AuthenticationProvider extends DaoAuthenticationProvider {

    public AuthenticationProvider(MyUserDetailService myUserDetailService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(myUserDetailService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //获取详细信息
        AuthenticaitonDetails details = (AuthenticaitonDetails) authentication.getDetails();
        //一旦发现验证码不正确，就立即抛出相应异常信息
        if(!details.isVerificationCodeIfAgree()){
            throw new RuntimeException("验证码不正确-------------------------");
        }
        //继续调用父类的方法完成密码验证
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
