package com.example.foodking.emotion;

import com.example.foodking.auth.CustomUserDetailsService;
import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.emotion.controller.EmotionController;
import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.recipe.controller.RecipeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(value = RecipeController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private EmotionService emotionService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach(){

    }

    @Test
    @DisplayName("댓글 이모션 토글 성공 : 이모션 추가성공")
    public void replyEmotiontoggleSuccess1(){

    }

    @Test
    @DisplayName("댓글 이모션 토글 성공 : 이모션 삭제성공")
    public void replyEmotiontoggleSuccess2(){

    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1l,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
