package com.example.foodking.auth;

import com.example.foodking.aop.enums.RedissonPrefix;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.response.LoginTokenRes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
// 이 클래스의 메소드중 트랜잭션이 필요한 메소드가 소수이기 때문에 클래스대신 메소드에 직접 선언해주었다.
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${JWT.Access.SecretKey}")
    private String accessSecretKey;

    @Value("${JWT.Refresh.SecretKey}")
    private String refreshSecretKey;

    @Qualifier("authRedis")
    private final RedisTemplate<String,String> authRedis;

    // 30분
    public Long validAccessTokenTime = 30 * 60 * 1000L;
    // 1달
    private Long validRefreshTokenTime = 30 * 24 * 60 * 60L;

    @PostConstruct
    protected void init() {
        /*
            secretKey에 특수문자가 있을 경우 통신할 때 문자는 os가 알아서 바이너리로 바꾼다.
            그러나 이러한 특수한 문자는 os마다 바꾸는 방법이 다를 수 있고 같은 secretKey를 쓰더라도 인증실패가 될 수 있다.
            따라서 애플리케이션에서 안전한 문자로 바꿔서 os에 전달하여 이러한 문제를 해결할 수 있다.
        */
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes(StandardCharsets.UTF_8));
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 엑세스토큰을 생성해서 반환하는 메소드
    public String createAccessToken(Long userId, Collection<? extends GrantedAuthority> roles) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roleList", roles);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // claim 저장
                .setIssuedAt(now) // 토큰 발행시간 저장
                .setExpiration(new Date(now.getTime() + validAccessTokenTime)) // 토큰 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    // 리프레시토큰을 생성해서 반환하는 메소드
    public String createRefreshToken(Long userId, Collection<? extends GrantedAuthority> roles) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roleList", roles);
        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims) // claim 저장
                .setIssuedAt(now) // 토큰 발행시간 저장
                .setExpiration(new Date(now.getTime() + validRefreshTokenTime)) // 토큰 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 암호화 알고리즘과, secret 값
                .compact();
        
        // 레디스에 RefreshToken 저장
        authRedis.opsForValue().set(
                RedissonPrefix.TOKEN_REDIS + String.valueOf(userId),
                refreshToken,
                validRefreshTokenTime,
                TimeUnit.SECONDS);

        return  refreshToken;
    }

    @Transactional
    public void logOut(Long userId, String accessToken){

        //Bearer 제거
        accessToken = accessToken.substring(7);

        //accessToken을 블랙리스트에 추가
        authRedis.opsForValue().set(
                RedissonPrefix.BLACK_LIST_REDIS + accessToken,
                String.valueOf(userId),
                validAccessTokenTime,
                TimeUnit.MILLISECONDS);

        //tokenRedis에서 refreshToekn삭제
        authRedis.delete(RedissonPrefix.TOKEN_REDIS + String.valueOf(userId));
    }

    // 토큰 재발급, 탈취에 대한 위험성을 최소화하기 위해 accessToken외에 refreshToken도 재발급을 한다.
    @Transactional
    public LoginTokenRes reissueToken(HttpServletRequest request){

        String refreshToken = findRefreshTokenByHeader(request);

        // refreshToken의 유효성검사
        if(refreshToken == null || !checkValidationRefreshToken(refreshToken))
            throw new CommondException(ExceptionCode.LOGIN_FAIL);

        // refreshToken에서 userId값 추출
        String userId = findUserIdByRefreshToken(refreshToken);

        // 레디스에 존재하는 토큰인지 확인
        String tokenInRedis = authRedis.opsForValue().get(RedissonPrefix.TOKEN_REDIS + userId);

        if(tokenInRedis == null || !tokenInRedis.equals(refreshToken))
            throw new CommondException(ExceptionCode.LOGIN_FAIL);

        User user = (User) customUserDetailsService.loadUserByUsername(userId);

        return LoginTokenRes.builder()
                .accessToken(createAccessToken(Long.valueOf(userId), user.getAuthorities()))
                .refreshToken(createRefreshToken(Long.valueOf(userId),user.getAuthorities()))
                .build();
    }

    // 토큰에서 추출한 userId값을 통해 인증객체를 생성하는 메소드
    @Transactional(readOnly = true)
    public Authentication findAuthenticationByAccessToken(String accessToken) {

        User user = (User) customUserDetailsService.loadUserByUsername(this.findUserIdByAccessToken(accessToken));
        return new UsernamePasswordAuthenticationToken(user.getUserId(),user.getPassword(),user.getAuthorities());
    }

    // Http헤더에서 AccessToken을 가져오는 메소드
    public String findAccessTokenByHeader(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        // Bear 제거 후 반환
        if(token != null && token.length() > 7 )
            return token.substring(7);

        return null;
    }

    // Http헤더에서 RefreshToken을 가져오는 메소드
    public String findRefreshTokenByHeader(HttpServletRequest request) {

        String token = request.getHeader("RefreshToken");
        
        // Bear 제거 후 반환
        if(token != null && token.length() > 7 )
            return token.substring(7);

        return null;
    }

    // AccessToken의 유효성을 검증하는 메소드
    public boolean checkValidationAccessToken(String accessToken) {
        try {
            String blackList = authRedis.opsForValue().get(RedissonPrefix.BLACK_LIST_REDIS + accessToken);
            if(blackList != null)
                return false;

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(accessSecretKey).build().parseClaimsJws(accessToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // RefreshToken의 유효성을 검증하는 메소드, 레디스에 존재하는 지 여부는 userId를 key로 하기때문에 reIssue메소드에서 수행한다.
    private boolean checkValidationRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(refreshSecretKey).build().parseClaimsJws(refreshToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // AccessToken에서 userId값을 추출하는 메소드
    private String findUserIdByAccessToken(String accessToken) {
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken).getBody().getSubject();
    }

    // RefreshToken에서 userId값을 추출하는 메소드
    private String findUserIdByRefreshToken(String refreshToken) {
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();
    }
}
