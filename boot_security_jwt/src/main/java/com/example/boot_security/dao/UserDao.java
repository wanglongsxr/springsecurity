package com.example.boot_security.dao;

import com.example.boot_security.model.Permission;
import com.example.boot_security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: wanglong
 * @time: 2020/3/18 17:18
 */
@Repository
public class UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    //根据账号查询用户信息
    public User getUserByUsername(String username){
        String sql = "select id,username,password,fullname,mobile from t_user where username = ?";
        //连接数据库查询用户
        List<User> list = jdbcTemplate.query(sql, new Object[]{username}, new BeanPropertyRowMapper<>(User.class));
        if(list !=null && list.size()==1){
            return list.get(0);
        }
        return null;
    }

    //根据用户id查询用户权限
    public List<String> findPermissionsByUserId(String userId){
        String sql = "SELECT * FROM t_permission WHERE id IN(" +
                "SELECT permission_id FROM t_role_permission WHERE role_id IN(" +
                "SELECT role_id FROM t_user_role WHERE user_id = ? ))";

        List<Permission> list = jdbcTemplate.query(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(Permission.class));
        List<String> permissions = new ArrayList<>();
        list.forEach(c -> permissions.add(c.getCode()));
        return permissions;
    }
}
