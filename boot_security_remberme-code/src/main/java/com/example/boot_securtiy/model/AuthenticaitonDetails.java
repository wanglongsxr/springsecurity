package com.example.boot_securtiy.model;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @description: 验证码属性details
 * @author: wanglong
 * @time: 2020/3/26 16:17
 */
@Data
public class AuthenticaitonDetails extends WebAuthenticationDetails {


    private static final long serialVersionUID = -6852436259731994805L;

    //验证码属性,false为验证码校验失败
    private boolean verificationCodeIfAgree = false;

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public AuthenticaitonDetails(HttpServletRequest request) {
        super(request);
        String captcha = request.getParameter("captcha");
        HttpSession session = request.getSession();
        String verificationCode = (String)session.getAttribute("captcha");
        if(!StringUtils.isEmpty(verificationCode)) {
            //清除验证码，不管是或成功
            session.removeAttribute("captcha");
            if (!StringUtils.isEmpty(verificationCode) && captcha.equals(verificationCode)) {
                this.verificationCodeIfAgree = true;
            }
        }
    }

    public AuthenticaitonDetails(HttpServletRequest request, String captcha){
        super(request);
        HttpSession session = request.getSession();
        String verificationCode = (String)session.getAttribute("captcha");
        if(!StringUtils.isEmpty(verificationCode)) {
            //清除验证码，不管是失败或成功
            session.removeAttribute("captcha");
            if (!StringUtils.isEmpty(captcha) && captcha.equals(verificationCode)) {
                this.verificationCodeIfAgree = true;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AuthenticaitonDetails)) return false;

        AuthenticaitonDetails that = (AuthenticaitonDetails) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(isVerificationCodeIfAgree(), that.isVerificationCodeIfAgree())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(isVerificationCodeIfAgree())
                .toHashCode();
    }
}
