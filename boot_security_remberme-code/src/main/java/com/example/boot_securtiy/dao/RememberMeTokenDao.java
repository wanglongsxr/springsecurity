package com.example.boot_securtiy.dao;

import com.example.boot_securtiy.model.RememberMeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @description: remember me暂时有错误，待解决
 * 如您可以帮解决错误，请立即联系我 qq:2647351651
 * @author: wanglong
 * @time: 2020/3/23 17:40
 */
@Repository
public class RememberMeTokenDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    //插入
    public void saveRememberMeToken(RememberMeToken token){
        String sql = "insert into t_remember_token(login_name, series, token, last_used ) values(?,?,?,?)";
        int i = jdbcTemplate.update(sql,token.getLoginName(),token.getSeries(),  token.getToken(), token.getLastUsed());
        if(i > 0){
            System.err.println("token 插入成功===============");
        }
    }
    public void deleteRememberMeToken(String loginName){
        String sql ="delete from t_remember_token where login_name = ?";
        int i = jdbcTemplate.update(sql, loginName);
        if(i > 0){
            System.err.println("token 删除成功===============");
        }
    }
    public void updateRememberMeToken(String series, String token, Date lastUsed){
        String sql = "update t_remember_token set token=?,last_used=? where series = ?";
        int i = jdbcTemplate.update(sql,token, lastUsed,series);
        if (i > 0) {
            System.err.println("token 更新成功===============");
        } else {
            System.err.println("token 更新失败===============");
        }
    }
    public RememberMeToken getRememberMeToken( String seriesId){
        String sql ="select login_name,series,token,last_used from t_remember_token where series=?";
        RememberMeToken rememberMeToken = jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(RememberMeToken.class), seriesId);
        return rememberMeToken;
    }
}
