package com.example.foodking.auth;

import com.example.foodking.common.RedissonPrefix;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.response.LoginTokenResDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${JWT.Access.SecretKey}")
    private String accessSecretKey;

    @Value("${JWT.Refresh.SecretKey}")
    private String refreshSecretKey;

    @Qualifier("tokenRedis")
    private final RedissonClient tokenRedis;

    @Qualifier("blackListRedis")
    private final RedissonClient blackListRedis;

    // 30분
    public Long validAccessTokenTime = 30 * 60 * 1000L;
    // 1달
    private Long validRefreshTokenTime = 30 * 24 * 60 * 60L;
    private final CustomUserDetailsService customUserDetailsService;


    @PostConstruct
    protected void init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }
    
    // 로그인 성공 시 토큰을 생성해서 반환하는 메소드
    public String createAccessToken(Long userId, Collection<? extends GrantedAuthority> roleList) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roleList", roleList);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // claim 저장
                .setIssuedAt(now) // 토큰 발행시간 저장
                .setExpiration(new Date(now.getTime() + validAccessTokenTime)) // 토큰 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    public String createRefreshToken(Long userId, Collection<? extends GrantedAuthority> roleList) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roleList", roleList);
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims) // claim 저장
                .setIssuedAt(now) // 토큰 발행시간 저장
                .setExpiration(new Date(now.getTime() + validRefreshTokenTime)) // 토큰 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 암호화 알고리즘과, secret 값
                .compact();
        
        // 레디스에 RefreshToken 저장
        RBucket<String> refreshTokenBucket = tokenRedis.getBucket
                (RedissonPrefix.TOKEN_REDIS + String.valueOf(userId));
        refreshTokenBucket.set(refreshToken, validRefreshTokenTime, TimeUnit.SECONDS);

        return  refreshToken;
    }
    
    // 토큰 재발급
    public LoginTokenResDTO reissueToken(String refreshToken){
        // refreshToken의 유효성검사
        if(!validateRefreshToken(refreshToken))
            throw new CommondException(ExceptionCode.LOGIN_FAIL);

        // refreshToken에서 userId값 추출
        String userId = getUserIdByRefreshToken(refreshToken);

        // 레디스에 존재하는 토큰인지 확인
        String tokenInRedis = (String) tokenRedis.getBucket(RedissonPrefix.TOKEN_REDIS + userId).get();

        if(tokenInRedis == null || !tokenInRedis.equals(refreshToken))
            throw new CommondException(ExceptionCode.LOGIN_FAIL);

        User user = (User) customUserDetailsService.loadUserByUsername(userId);

        return LoginTokenResDTO.builder()
                .accessToken(createAccessToken(Long.valueOf(userId), user.getAuthorities()))
                .refreshToken(createRefreshToken(Long.valueOf(userId),user.getAuthorities()))
                .build();
    }

    // Http헤더에서 AccessToken을 가져오는 메소드
    public String resolveAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if(token == null || !token.substring(0,7).equals("Bearer "))
            return null;

        return token.substring(7);
    }

    // 토큰에서 추출한 userId값을 통해 인증객체를 생성하는 메소드
    public Authentication getAuthenticationByAccessToken(String token) {
        User user = (User) customUserDetailsService.loadUserByUsername(this.getUserIdByAccessToken(token));
        return new UsernamePasswordAuthenticationToken(user.getUserId(),user.getPassword(),user.getAuthorities());
    }

    // AccessToken의 유효성을 검증하는 메소드
    public boolean validateAccessToken(String token) {
        try {
            RBucket<String> blackList = blackListRedis.getBucket(RedissonPrefix.BLACK_LIST_REDIS + token);
            if(blackList.get() != null)
                return false;

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(accessSecretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // RefreshToken의 유효성을 검증하는 메소드
    private boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(refreshSecretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // AccessToken에서 userId값을 추출하는 메소드
    private String getUserIdByAccessToken(String token) {
        log.info(token);
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // RefreshToken에서 userId값을 추출하는 메소드
    private String getUserIdByRefreshToken(String token) {
        log.info(token);
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token).getBody().getSubject();
    }
}
