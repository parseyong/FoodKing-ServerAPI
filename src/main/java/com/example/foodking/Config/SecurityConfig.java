package com.example.foodking.Config;

import com.example.foodking.Auth.JwtAuthenticationFilter;
import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Exception.Handler.CustomAccessDeniedHandler;
import com.example.foodking.Exception.Handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/login","/messages","/nickname/check","/email/check","/email/find","/password/find","/api/v1/auth/**","/",
                        "/v2/api-docs", "/swagger-resources/**", "/swagger-ui/index.html", "/swagger-ui.html","/webjars/**", "/swagger/**").permitAll()
                .antMatchers(HttpMethod.POST,"/users").permitAll()
                .antMatchers("/**").hasRole("USER")
                .anyRequest().authenticated()

                .and().exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                .and().exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)

                .csrf().disable();

        return http.build();
    }
}
