package com.green.eats.common.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="constants.jwt")
public record ConstJwt(String issuer
        , String bearerFormat
        , String claimKey
        , String secretKey
        , String accessTokenCookieName
        , String accessTokenCookiePath
        , int accessTokenCookieValiditySeconds
        , long accessTokenValidityMilliseconds
        , String refreshTokenCookieName
        , String refreshTokenCookiePath
        , int refreshTokenCookieValiditySeconds
        , long refreshTokenValidityMilliseconds) {}
