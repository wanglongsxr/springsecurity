package com.example.boot_security.authentication;


import com.alibaba.fastjson.JSON;
import com.example.boot_security.config.JwtProperties;
import com.example.boot_security.model.JwtRedisEnum;
import com.example.boot_security.util.GenerateModelMap;
import com.example.boot_security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: 自定义鉴定成功处理器
 * 用户信息校验正确生成token，并存储到redis中（引入redis的原因是为了token的续签以及注销登录）
 * @author: wanglong
 * @time: 2020/3/21 9:55
 */
@Component
@Slf4j
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String randomKey = jwtTokenUtil.getRandomKey();//增加token安全系数
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("username:{}",username);
        final String token = jwtTokenUtil.generateToken(username, randomKey);
        System.err.println("生成的token为："+token);
        System.err.println("登录成功~~~~~~~~~~~~~~~~~~~··");
        // 判断是否开启允许多人同账号同时在线，若不允许的话则先删除之前的
        if (jwtProperties.isPreventsLogin()) {
            // T掉同账号已登录的用户token信息
            batchDel(JwtRedisEnum.getTokenKey(username, "*"));
            // 删除同账号已登录的用户认证信息
            batchDel(JwtRedisEnum.getAuthenticationKey(username, "*"));
        }
        // 存到redis
        redisTemplate.opsForValue().set(JwtRedisEnum.getTokenKey(username, randomKey),
                token,
                jwtProperties.getExpiration(),
                TimeUnit.SECONDS);
        //将认证信息存到redis，方便后期业务用，也方便 JwtAuthenticationTokenFilter用
        redisTemplate.opsForValue().set(JwtRedisEnum.getAuthenticationKey(username, randomKey),
                JSON.toJSONString(authentication),
                jwtProperties.getExpiration(),
                TimeUnit.SECONDS
        );
        ModelMap modelMap = GenerateModelMap.generateMap(HttpStatus.OK.value(), "登陆成功");
        modelMap.put("currentUser", authentication);
        response.setHeader(tokenHeader, tokenHead +token);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(modelMap));
    }

    /**
     * 批量删除redis的key
     *
     * @param key：redis-key
     */
    private void batchDel(String key) {
        Set<String> set = redisTemplate.keys(key);
        for (String keyStr : Objects.requireNonNull(set)) {
            log.info("keyStr{}", keyStr);
            redisTemplate.delete(keyStr);
        }
    }
}
