package com.example.boot_security.authorize.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface AuthorizeProvider {

    void config(HttpSecurity httpSecurity) throws Exception;
}
