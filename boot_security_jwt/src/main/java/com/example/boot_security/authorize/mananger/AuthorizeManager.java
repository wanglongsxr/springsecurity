package com.example.boot_security.authorize.mananger;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//providers管理类接口
public interface AuthorizeManager {

    void config(HttpSecurity httpSecurity) throws Exception;;
}
