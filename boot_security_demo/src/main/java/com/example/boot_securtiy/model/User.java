package com.example.boot_securtiy.model;

import lombok.Data;

/**
 * @description: 用户类
 * @author: wanglong
 * @time: 2020/3/18 17:16
 */
@Data
public class User {
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
