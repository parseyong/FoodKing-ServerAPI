package com.example.foodking.auth;

import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = JwtController.class)
@Import(SecurityConfig.class)
public class JwtControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("accessToken 재발급 테스트 -> 성공")
    public void tokenReissueTest1() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(post("/refresh-token/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공!"))
                .andDo(print());

        verify(jwtProvider,times(1)).reissueToken(any());
    }

    @Test
    @DisplayName("accessToken 재발급 테스트 -> (실패 : 로그인 실패)")
    public void tokenReissueTest2() throws Exception {
        //given
        given(jwtProvider.reissueToken(any()))
                .willThrow(new CommondException(ExceptionCode.LOGIN_FAIL));

        //when,then
        this.mockMvc.perform(post("/refresh-token/reissue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("로그인에 실패하였습니다."))
                .andDo(print());

        verify(jwtProvider,times(1)).reissueToken(any());
    }

    @Test
    @DisplayName("로그아웃 테스트 -> 성공")
    public void logOutTest1() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(post("/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 성공!"))
                .andDo(print());

        verify(jwtProvider,times(1)).logOut(any(Long.class),any());
    }

    @Test
    @DisplayName("로그아웃 테스트 -> (실패: 인증 실패)")
    public void logOutTest2() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(post("/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(jwtProvider,times(0)).logOut(any(Long.class),any());
    }
    
    private void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
