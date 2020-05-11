package com.example.boot_securtiy.authorize;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * @description: 授权决策管理器
 * @author: wanglong
 * @time: 2020/5/11 20:10
 */
@Service
public class AuthorizeAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if(CollectionUtils.isEmpty(configAttributes)) return;
        //在这里注意一下java8 for循坏中的return表示跳出当前循坏，相当于非java8的continue，
        //非java8 for循坏中的return代表终止循坏，相当于时break的作用；
//        configAttributes.stream().forEach(configAttribute ->{
//            String needRole = configAttribute.getAttribute();
//            authentication.getAuthorities().stream().forEach(b ->{
//                if(needRole.trim().equals(b.getAuthority())) return;
//            });
//        });
        String needRole;
        for (ConfigAttribute configAttribute : configAttributes) {
            needRole = configAttribute.getAttribute();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    /**
     * @description 方法在启动的时候被AbstractSecurityInterceptor调用，
     * 决定了AccessDecisionManager，是否可以执行传递ConfigAttribute
     * @author wanglong
     * @date 2020/5/11 20:16
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    /**
     * @description 被安全拦截器的实现调用，包含安全拦截器将显示的AccessDecisionManager支持安全对象的类型。
     * @author wanglong
     * @date 2020/5/11 20:16
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
