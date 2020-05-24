package com.example.boot_security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jwt的基本配置
 *
 */
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    /**
     * 默认前面秘钥
     */
    private String secret;

    /**
     * token默认有效期时长，1小时
     */
    private Long expiration =3600L;

    /**
     * token的唯一标记，目前用于redis里存储，解决同账号，同时只能单用户登录的情况
     */
    private String md5Key;

    /**
     * token还剩余多长时间就自动刷新下，默认是600s
     */
    private Long autoRefreshTokenExpiration = 600L;

    /**
     * 判断是否开启允许多人同账号同时在线，若不允许的话则将上一个人T掉，默认false，不T掉，允许多人登录，true：T掉
     */
    private boolean preventsLogin;

    /**
     * GET请求是否需要进行Authentication请求头校验，true：默认校验；false：不拦截GET请求
     */
    private boolean preventsGetMethod;

}
