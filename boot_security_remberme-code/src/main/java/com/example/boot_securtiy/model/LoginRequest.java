package com.example.boot_securtiy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 登录model对象
 * @author: wanglong
 * @time: 2020/3/26 20:27
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;
    private String captcha;

    public boolean isInvalid(){
        return StringUtils.isBlank(username)||StringUtils.isBlank(password)||StringUtils.isBlank(captcha);
    }
}