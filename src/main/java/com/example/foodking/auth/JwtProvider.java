package com.example.foodking.auth;

import com.example.foodking.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
@Log4j2
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${JWT.SecretKey}")
    private String secretKey;
    private long validTokenTime = 30 * 60 * 1000L;
    private final CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    // 로그인 성공 시 토큰을 생성해서 반환하는 메소드
    public String createToken(Long userId, Collection<? extends GrantedAuthority> roleList) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("roleList", roleList);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // claim 저장
                .setIssuedAt(now) // 토큰 발행시간 저장
                .setExpiration(new Date(now.getTime() + validTokenTime)) // 토큰 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    // 토큰에서 인증객체를 추출하는 메소드
    public Authentication getAuthentication(String token) {
        User user = (User) customUserDetailsService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(user.getUserId(),user.getPassword(),user.getAuthorities());
    }

    public String getUserId(String token) {
        log.info(token);
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰의 유효성을 검증하는 메소드
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Http헤더에서 인증토큰을 가져오는 메소드
    public String resolveToken(HttpServletRequest request) {

        return request.getHeader("Auth");
    }

    // SecurityContextHolder에 저장된 인증정보 중 Principal(userId)값을 반환하는 정적메소드
    public static Long getUserId(){
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
