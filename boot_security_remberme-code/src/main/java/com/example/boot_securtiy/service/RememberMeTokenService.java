package com.example.boot_securtiy.service;

import com.example.boot_securtiy.dao.RememberMeTokenDao;
import com.example.boot_securtiy.model.RememberMeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @description: "记住我"的逻辑层（自定义数据库表名）
 * 原理是 重写JdbcTokenRepositoryImpl的resposity方式，从而让dao成为自定义库名
 *
 * remember me的原理是：
 * 成功登录后：将使用一些随机哈希为用户创建一个永久令牌。将为用户创建一个cookie，上面带有令牌详细信息。为用户创建一个会话。只要用户仍具有活动会话，就不会在身份验证时调用我的功能。
 *
 * 用户会话到期后： “记住我”功能将启动并使用cookie从数据库中获取持久性令牌。如果持久令牌与cookie中的令牌匹配，则每个人都对用户进行身份验证感到满意，将生成一个新的随机哈希，并使用持久令牌进行更新，并为后续请求更新用户的cookie。
 *
 * 但是，如果Cookie中的令牌与持久令牌中的令牌不匹配，则会收到CookieTheftException。令牌不匹配的最常见原因是快速连续触发了两个或更多请求，第一个请求将通过，并为随后的请求生成新的哈希，但第二个请求仍将旧令牌打开它并因此导致异常。
 * 具体详情在PersistentTokenBasedRememberMeServices的processAutoLoginCookie方法内
 *
 * @author: wanglong
 * @time: 2020/3/23 17:31
 */
@Service
public class RememberMeTokenService implements PersistentTokenRepository {

    @Autowired
    private RememberMeTokenDao rememberMeTokenDao;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        rememberMeTokenDao.saveRememberMeToken(new RememberMeToken(token.getSeries(),token.getUsername(),token.getTokenValue(),token.getDate()));
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        rememberMeTokenDao.updateRememberMeToken(series,tokenValue,lastUsed);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        RememberMeToken rememberMeToken = rememberMeTokenDao.getRememberMeToken(seriesId);
        return  Optional.ofNullable(rememberMeToken).map(a ->{
            return new PersistentRememberMeToken(a.getLoginName(),a.getSeries(),a.getToken(),a.getLastUsed());
        }).orElse(null);
    }

    @Transactional
    @Override
    public void removeUserTokens(String username) {
        rememberMeTokenDao.deleteRememberMeToken(username);
    }
}
