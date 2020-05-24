package com.example.boot_security.controller;

import com.example.boot_security.model.JwtRedisEnum;
import com.example.boot_security.util.GenerateModelMap;
import com.example.boot_security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 退出清空jwt
 * @author: wanglong
 * @time: 2020/5/24 10:30
 */
@RestController
@Slf4j
public class LogoutController {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenUtil jwtTokenUtil;

    public LogoutController(StringRedisTemplate redisTemplate, JwtTokenUtil jwtTokenUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 退出登录
     * 双清
     */
    @GetMapping("/jwtLogout")
    public ResponseEntity<ModelMap> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        // 为嘛无需判断authHeader是否为null？ 因为Jwt过滤器已经判断过了。
        String authToken = authHeader.substring("Bearer ".length());
        String randomKey = jwtTokenUtil.getMd5KeyFromToken(authToken);
        String username = jwtTokenUtil.getUsernameFromToken(authToken);
        redisTemplate.delete(JwtRedisEnum.getTokenKey(username, randomKey));
        redisTemplate.delete(JwtRedisEnum.getAuthenticationKey(username, randomKey));
        log.info("删除{}成功", JwtRedisEnum.getTokenKey(username, randomKey));
        log.info("退出成功");
        ModelMap modelMap = GenerateModelMap.generateMap(HttpStatus.OK.value(), "退出成功！");
        return ResponseEntity.ok(modelMap);
    }
}
