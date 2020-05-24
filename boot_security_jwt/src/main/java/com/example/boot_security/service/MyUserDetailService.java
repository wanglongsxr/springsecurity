package com.example.boot_security.service;

import com.example.boot_security.dao.UserDao;
import com.example.boot_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @description: 自定义用户信息类
 * @author: wanglong
 * @time: 2020/3/17 14:41
 */
@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //登录用户
        System.out.println("userName:"+username);
        //从数据库查询用户(数据库默认只有张三，密码为123，权限为p1,只能访问r1资源)
        User user = userDao.getUserByUsername(username);
        UserDetails userDetails = Optional.ofNullable(user).map(a -> {
            //查询用户的权限
            List<String> permissions = userDao.findPermissionsByUserId(a.getId());
            String[] permissionArray = new String[permissions.size()];
            permissions.toArray(permissionArray);

            //返回userDetail，用它去与页面上的输入值作比较
            return org.springframework.security.core.userdetails.User.withUsername(a.getFullname()).password(a.getPassword()).authorities(permissionArray).build();
        }).orElse(null);
        return userDetails;
    }
}
