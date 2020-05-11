package com.example.boot_securtiy.authorize;

import com.example.boot_securtiy.dao.PermissionDao;
import com.example.boot_securtiy.dao.UserDao;
import com.example.boot_securtiy.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @description: 实现了动态权限验证
 * @author: wanglong
 * @time: 2020/5/9 22:35
 */
@Service
public class AuthorizeSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private PermissionDao permissionDao;

    private static Map<String, Collection<ConfigAttribute>> resourceMap = null;

    /**
     * @description 提取系统中的所有权限,存入map
     * @return void
     * @author wanglong
     * @date 2020/5/9 23:22
     */
    public void loadResourceDefine(){
        resourceMap =new HashMap<>();
        List<Permission> allPermissions = permissionDao.findAllPermissions();
        allPermissions.stream().forEach(a ->resourceMap.put(a.getUrl(), Arrays.asList(new SecurityConfig(a.getCode()))));
    }

    /**
     * @description 授权时调用，根据url,匹配相关权限
     * @return java.util.Collection<org.springframework.security.access.ConfigAttribute>
     * @author wanglong
     * @date 2020/5/9 23:38
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(Objects.isNull(resourceMap)) loadResourceDefine();
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        AntPathRequestMatcher matcher;
        String resurl;
        for(String key:resourceMap.keySet()){
            resurl = key;
            matcher=new AntPathRequestMatcher(resurl);
            if(matcher.matches(request)){
                return resourceMap.get(key);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Collections.emptyList();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
