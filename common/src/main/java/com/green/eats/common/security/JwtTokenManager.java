package com.green.eats.common.security;

import com.green.eats.common.constants.ConstJwt;
import com.green.eats.common.model.JwtUser;
import com.green.eats.common.model.UserPrincipal;
import com.green.eats.common.redis.RedisService;
import com.green.eats.common.utils.MyCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(Authentication.class) //클래스패스에 특정 클래스(예: Authentication)가 존재할 때만 빈으로 등록
public class JwtTokenManager { //인증 처리 총괄
    private final ConstJwt constJwt;
    private final MyCookieUtils myCookieUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public void issue(HttpServletResponse res, JwtUser jwtUser) {
        setAccessTokenInCookie(res, jwtUser);
        setRefreshTokenInCookie(res, jwtUser);
    }

    public void setAccessTokenInCookie(HttpServletResponse res, JwtUser jwtUser) {
        String accessToken = jwtTokenProvider.generateAccessToken(jwtUser);
        setAccessTokenInCookie(res, accessToken);
    }

    public void setRefreshTokenInCookie(HttpServletResponse res, JwtUser jwtUser) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(jwtUser);
        String redisKey = String.format("RT-%d", jwtUser.getSignedUserId());
        redisService.save(redisKey, refreshToken, constJwt.refreshTokenCookieValiditySeconds());
        setRefreshTokenInCookie(res, refreshToken);
    }

    //AT를 쿠키에 담는다.
    public void setAccessTokenInCookie(HttpServletResponse res, String accessToken) {
        myCookieUtils.setCookie(res
                            , constJwt.accessTokenCookieName()
                            , accessToken
                            , constJwt.accessTokenCookieValiditySeconds()
                            , constJwt.accessTokenCookiePath()
        );
    }

    //RT를 쿠키에 담는다.
    public void setRefreshTokenInCookie(HttpServletResponse res, String refreshToken) {
        myCookieUtils.setCookie(res
                             , constJwt.refreshTokenCookieName()
                             , refreshToken
                             , constJwt.refreshTokenCookieValiditySeconds()
                             , constJwt.refreshTokenCookiePath()
        );
    }

    //AT를 쿠키에서 꺼낸다.
    public String getAccessTokenFromCookie(HttpServletRequest req) {
        return myCookieUtils.getValue(req, constJwt.accessTokenCookieName());
    }

    //RT를 쿠키에서 꺼낸다.
    public String getRefreshTokenFromCookie(HttpServletRequest req) {
        return myCookieUtils.getValue(req, constJwt.refreshTokenCookieName());
    }



    //시큐리티에서 로그인 인정을 할 때 이 객체를 Security Context Holder(공간)에 담으면
    //시큐리티는 인증이 되었다고 처리한다.
    //import org.springframework.security.core.Authentication;
    public Authentication getAuthentication(HttpServletRequest req) {
        String accessToken = getAccessTokenFromCookie(req); //AT를 쿠키에서 빼낸다.
        if(accessToken == null) { return null; }
        //쿠키에 AT이 있다. JWT에 담았던 JwtUser객체를 다시 빼낸다.
        JwtUser jwtUser = jwtTokenProvider.getJwtUserFromToken(accessToken);
        UserPrincipal userPrincipal = new UserPrincipal(jwtUser);

        return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    public void deleteAccessTokenInCookie(HttpServletResponse res) {
        myCookieUtils.deleteCookie(res, constJwt.accessTokenCookieName(), constJwt.accessTokenCookiePath());
    }

    public void deleteRefreshTokenInCookie(HttpServletResponse res) {
        myCookieUtils.deleteCookie(res, constJwt.refreshTokenCookieName(), constJwt.refreshTokenCookiePath());
    }

    public void signOut(HttpServletResponse res) {
        deleteAccessTokenInCookie(res);
        deleteRefreshTokenInCookie(res);
    }

    public void reissue(HttpServletRequest req, HttpServletResponse res) {
        //req에서 RT을 얻어야 한다.
        String refreshToken = getRefreshTokenFromCookie(req);



        //RT를 이용하여 JwtUser 객체를 만든다.
        JwtUser jwtUser = jwtTokenProvider.getJwtUserFromToken(refreshToken);

        //Redis에 refreshToken있는지

        //JwtUser를 이용하여 AT를 만들어 cookie에 담아주세요.
        setAccessTokenInCookie(res, jwtUser);
    }
}
