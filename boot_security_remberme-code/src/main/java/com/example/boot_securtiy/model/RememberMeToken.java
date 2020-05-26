package com.example.boot_securtiy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description: remember me
 * @author: wanglong
 * @time: 2020/3/23 17:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RememberMeToken {

    private String series;

    private String loginName;

    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date lastUsed;
}
