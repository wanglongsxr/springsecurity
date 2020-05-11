package com.example.boot_securtiy.authorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;

/**
 * @description: 资源访问过滤器（默认的过滤器是FilterSecurityInterceptor）
 * AbstractSecurityInterceptor，其下有两个实现类：
 *      MethodSecurityInterceptor 将用于受保护的方法
 *      FilterSecurityInterceptor 用于受保护的web 请求
 *
 * 该类重写doFilter方法，注意要注入权限类FilterInvocationSecurityMetadataSource，和setAccessDecisionManager(AccessDecisionManager)
 * @author: wanglong
 * @time: 2020/5/9 22:28
 */
@Service
public class AuthorizeSecurityInterceptor extends AbstractSecurityInterceptor implements Filter{

    @Autowired
    private AuthorizeSecurityMetadataSource authorizeSecurityMetadataSource;

    @Autowired
    public void setMyAccessDecisionManager(AuthorizeAccessDecisionManager authorizeAccessDecisionManager) {
        super.setAccessDecisionManager(authorizeAccessDecisionManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        invoke(new FilterInvocation(request, response, chain));
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        //fi里面有一个被拦截的url
        //里面调用MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法获取fi对应的所有权限
        //再调用MyAccessDecisionManager的decide方法来校验用户的权限是否足够
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            //执行下一个拦截器
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }



    @Override
    public void init(FilterConfig filterConfig) {
        System.err.println("----------AuthorizeSecurityInterceptor.init()--------------------------");
    }

    @Override
    public void destroy() {
        System.err.println("----------AuthorizeSecurityInterceptor.destroy()----------------------");
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.authorizeSecurityMetadataSource;
    }
}
