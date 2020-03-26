package com.example.boot_securtiy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description: remember me暂时有错误，待解决
 * 如您可以帮解决错误，请立即联系我 qq:2647351651
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

    private Date lastUsed;
}
