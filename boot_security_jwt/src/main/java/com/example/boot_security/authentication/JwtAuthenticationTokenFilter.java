package com.example.boot_security.authentication;

import com.alibaba.fastjson.JSON;
import com.example.boot_security.authorize.provider.AuthorizeCoreProvider;
import com.example.boot_security.config.JwtProperties;
import com.example.boot_security.model.JwtRedisEnum;
import com.example.boot_security.model.JwtUrlEnum;
import com.example.boot_security.util.GenerateModelMap;
import com.example.boot_security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @description: jwt作为会话管理
 * 默认拦截所有请求，判断token是否合法，是否过期
 * @author: wanglong
 * @time: 2020/5/17 18:54
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthorizeCoreProvider authorizeCoreProvider;

    private final JwtProperties jwtProperties;

    private final StringRedisTemplate redisTemplate;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationTokenFilter(JwtTokenUtil jwtTokenUtil, AuthorizeCoreProvider authorizeCoreProvider, JwtProperties jwtProperties, StringRedisTemplate redisTemplate) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authorizeCoreProvider = authorizeCoreProvider;
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (antPathMatcher.match("/favicon.ico", request.getRequestURI())) {
            log.info("jwt不拦截此路径：{}，请求方式为：{}", request.getRequestURI(), request.getMethod());
            filterChain.doFilter(request, response);
            return;
        }
        /*
         * get请求是否需要进行Authentication请求头校验，true：默认校验；false：不拦截GET请求
         * 因为get请求比较特殊
         */
        if (!jwtProperties.isPreventsGetMethod()) {
            if (Objects.equals(RequestMethod.GET.toString(), request.getMethod())) {
                log.info("jwt不拦截此路径因为开启了不拦截GET请求：{}，请求方式为：{}", request.getRequestURI(), request.getMethod());
                filterChain.doFilter(request, response);
                return;
            }
        }
        /*
         * 排除路径，并且如果是options请求是cors跨域预请求，设置allow对应头信息
         * 这里的 getPermitUrls 是在 com.example.boot_security.authorize.provider.AuthorizeCoreProvider中设置的
         */
        String[] permitUrls = getPermitUrls();
        for (String permitUrl : permitUrls) {
            if (antPathMatcher.match(permitUrl, request.getRequestURI())
                    || Objects.equals(RequestMethod.OPTIONS.toString(), request.getMethod())) {
                log.info("jwt不拦截此路径：{}，请求方式为：{}", request.getRequestURI(), request.getMethod());
                filterChain.doFilter(request, response);
                return;
            }
        }


        // 获取Authorization
        String authHeader = request.getHeader(tokenHeader);
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(tokenHead)) {
            log.error("Authorization的开头不是Bearer，Authorization===>{}", authHeader);
            responseEntity(response, HttpStatus.UNAUTHORIZED.value(), "暂无权限！");
            return;
        }

        // 截取token()
        String authToken = authHeader.substring(tokenHead.length()+1);

        /*
         * 判断token是否失效
         */
        if (jwtTokenUtil.isTokenExpired(authToken)) {
            log.info("token已过期！");
            responseEntity(response, HttpStatus.UNAUTHORIZED.value(), "token已过期！");
            return;
        }

        String randomKey = jwtTokenUtil.getMd5KeyFromToken(authToken);
        String username = jwtTokenUtil.getUsernameFromToken(authToken);

        /*
         * 验证token是否合法
         */
        if (StringUtils.isBlank(username) || StringUtils.isBlank(randomKey)) {
            log.info("username{}或randomKey{} 可能为null！", username, randomKey);
            responseEntity(response, HttpStatus.UNAUTHORIZED.value(), "暂无权限！");
            return;
        }

        /*
         *验证token是否存在（过期了也会消失）
         */
        Object token = redisTemplate.opsForValue().get(JwtRedisEnum.getTokenKey(username, randomKey));
        System.err.println("redis中的token值为："+token);
        if (Objects.isNull(token)) {
            log.info("Redis里没查到key{}对应的value！", JwtRedisEnum.getTokenKey(username, randomKey));
            responseEntity(response, HttpStatus.UNAUTHORIZED.value(), "token已过期！");
            return;
        }

        /*
         * 判断传来的token和存到redis的token是否一致
         */
        if (!Objects.equals(token.toString(), authToken)) {
            log.error("前端传来的token{}和redis里的token{}不一致！", authToken, token.toString());
            responseEntity(response, HttpStatus.UNAUTHORIZED.value(), "暂无权限！");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String authenticationStr = redisTemplate.opsForValue().get(JwtRedisEnum.getAuthenticationKey(username, randomKey));
            Authentication authentication = JSON.parseObject(authenticationStr, Authentication.class);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // token过期时间
        long tokenExpireTime = jwtTokenUtil.getExpirationDateFromToken(authToken).getTime();

        // token还剩余多少时间过期
        long surplusExpireTime = (tokenExpireTime - System.currentTimeMillis()) / 1000;
        log.info("Token剩余时间:" + surplusExpireTime);

        /*
         * 退出登录不刷新token，因为假设退出登录操作，刷新token了，这样清除的是旧的token，相当于根本没退出成功
         */
        if (!StringUtils.equals(request.getRequestURL(), JwtUrlEnum.LOGOUT.url())) {
            // token过期时间小于等于多少秒，自动刷新token
            if (surplusExpireTime <= jwtProperties.getAutoRefreshTokenExpiration()) {
                // 1.删除之前的token
                redisTemplate.delete(JwtRedisEnum.getTokenKey(username, randomKey));
                //2.重新生成token
                //3.重新生成randomKey，放到header以及redis
                String newRandomKey = jwtTokenUtil.getRandomKey();
                // 重新生成token，放到header以及redis
                String newAuthToken = jwtTokenUtil.refreshToken(authToken, newRandomKey);
                response.setHeader(tokenHeader, tokenHead + newAuthToken);
                response.setHeader("randomKey", newRandomKey);
                redisTemplate.opsForValue().set(JwtRedisEnum.getTokenKey(username, newRandomKey),
                        newAuthToken,
                        jwtProperties.getExpiration(),
                        TimeUnit.SECONDS);
                log.info("重新生成token{}和randomKey{}", newAuthToken, newRandomKey);

                // 取出老的authenticate放到redis里
                String authentication = redisTemplate.opsForValue().get(JwtRedisEnum.getAuthenticationKey(username, randomKey));
                redisTemplate.opsForValue().set(JwtRedisEnum.getAuthenticationKey(username, newRandomKey),
                        authentication,
                        jwtProperties.getExpiration(),
                        TimeUnit.SECONDS);

                // 删除旧的认证信息,这里设置50s后自动到期，是因为在实际应用中有可能从authentication里获取用户唯一标识
                redisTemplate.opsForValue().set(
                        JwtRedisEnum.getAuthenticationKey(username, randomKey),
                        authentication,
                        50,
                        TimeUnit.SECONDS);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取需要过滤的url
     *
     * @return
     */
    private String[] getPermitUrls() {
        /* 核心模块相关的URL */
        String[] corePermitUrls = authorizeCoreProvider.getPermitUrls();
        /* 返回的数组 */
        return ArrayUtils.addAll(corePermitUrls);
    }

    private void responseEntity(HttpServletResponse response, Integer status, String message) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        ModelMap modelMap = GenerateModelMap.generateMap(status, message);
        try {
            response.getWriter().write(
                    JSON.toJSONString(modelMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
