package com.example.boot_security.model;

import lombok.Data;

/**
 * @description:
 * @author: wanglong
 * @time: 2020/3/19 12:06
 */
@Data
public class Permission {
    private String id;
    private String code;
    private String description;
    private String url;
}
