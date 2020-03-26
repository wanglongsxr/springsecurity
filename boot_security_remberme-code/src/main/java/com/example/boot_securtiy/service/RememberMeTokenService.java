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
 * remember me暂时有错误，待解决
 *
 * 该方式会抛出一个Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack错误，即为Cookie被盗的异常
 * 产生该错误的方式：首先第一次登录用户，跳转到首页，然后再重启服务器，再直接打开首页直接弹出该错误
 * 产生原因为：当cookie请求到过滤器的时候，直接走RememberMeAuthenticationFilter，没有上下文信息直接走ememberMeServices.autoLogin(request,esponse);
 * 然后拿到cookie，从而判断与数据库的token是否一致，一致之后更新token，问题就是出现在这，更新完之后，它会用旧的token与cookie值比，肯定不一致，所以抛出异常
 * 具体详情在PersistentTokenBasedRememberMeServices的processAutoLoginCookie方法内
 *
 * 如您可以帮解决错误，请立即联系我 qq:2647351651
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
