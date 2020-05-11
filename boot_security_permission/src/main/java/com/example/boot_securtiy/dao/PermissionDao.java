package com.example.boot_securtiy.dao;

import com.example.boot_securtiy.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: wanglong
 * @time: 2020/5/9 23:14
 */
@Repository
public class PermissionDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Permission> findAllPermissions(){
        String sql = "SELECT * FROM t_permission" ;
        List<Permission> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Permission.class));
        return list;
    }
}
