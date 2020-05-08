package com.example.boot_securtiy.authentication;

import com.example.boot_securtiy.service.MyUserDetailService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        //禁止用户不存在不抛异常
        this.setHideUserNotFoundExceptions(false);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();
        UserDetails userDetails = this.getUserDetailsService().loadUserByUsername(username);
        // 验证密码是否正确
        if (!new BCryptPasswordEncoder().matches((CharSequence) token.getCredentials(), userDetails.getPassword())) {
            throw new AuthenticationServiceException("用户名或密码错误！！！");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
