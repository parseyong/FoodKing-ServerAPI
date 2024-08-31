package com.example.foodking.emotion;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.emotion.controller.EmotionController;
import com.example.foodking.emotion.dto.req.EmotionToggleReq;
import com.example.foodking.emotion.enums.EmotionType;
import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.google.gson.Gson;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = EmotionController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    /*
        JwtAuthenticationFilter클래스는 Filter이므로 @WebMvcTest에 스캔이 되지만 JwtProvider클래스는
        @Component로 선언되어있으므로 @WebMvcTest의 스캔대상이 아니다.
        따라서 JwtAuthenticationFilter클래스에서 JwtProvider 빈을 가져올 수 없어 테스트가 정상적으로 수행되지 않는다.
        따라서 JwtProvider를 Mock객체로 대체하여 해당 문제를 해결하였다.
    */
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private EmotionService emotionService;
    @Autowired
    private MockMvc mockMvc;

    private Gson gson = new Gson();

    @Test
    @DisplayName("댓글 이모션 토글 테스트 -> 성공")
    public void replyEmotiontoggleTest1() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/replies/{replyId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이모션 토글 완료"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : PathVariable 타입예외")
    public void replyEmotiontoggleTest4() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/replies/{replyId}/emotions","test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("replyId이 Long타입이여야 합니다."))
                .andDo(print());

        verify(emotionService,times(0))
                .toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : 존재하지 않는 댓글")
    public void replyEmotiontoggleTest5() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_REPLY))
                .when(emotionService).toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));

        //when,then
        this.mockMvc.perform(post("/replies/{replyId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 댓글입니다"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : 권한이 없는 이모션")
    public void replyEmotiontoggleTest6() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION))
                .when(emotionService).toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));

        //when,then
        this.mockMvc.perform(post("/replies/{replyId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 이모션에 대한 권한이 없습니다"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : 인증실패")
    public void replyEmotiontoggleTest7() throws Exception {
        //given
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/replies/{replyId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(emotionService,times(0))
                .toggleReplyEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("레시피 이모션 토글 테스트 -> 성공")
    public void recipeEmotiontoggleTest1() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/recipes/{recipeInfoId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피이모션 토글 완료"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("레시피 이모션 토글 실패 : PathVariable 타입예외")
    public void recipeEmotiontoggleTest4() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/recipes/{recipeInfoId}/emotions","test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andDo(print());

        verify(emotionService,times(0))
                .toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("레시피 이모션 토글 실패 : 존재하지 않는 레시피")
    public void recipeEmotiontoggleTest5() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO))
                .when(emotionService).toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));

        //when,then
        this.mockMvc.perform(post("/recipes/{recipeInfoId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : 권한이 없는 이모션")
    public void recipeEmotiontoggleTest6() throws Exception {
        //given
        makeAuthentication();
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION))
                .when(emotionService).toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));

        //when,then
        this.mockMvc.perform(post("/recipes/{recipeInfoId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 이모션에 대한 권한이 없습니다"))
                .andDo(print());

        verify(emotionService,times(1))
                .toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    @Test
    @DisplayName("댓글 이모션 토글 실패 : 인증실패")
    public void recipeEmotiontoggleTest7() throws Exception {
        //given
        String requestBody = gson.toJson( EmotionToggleReq.builder().emotionType(EmotionType.LIKE).build() );

        //when,then
        this.mockMvc.perform(post("/recipes/{recipeInfoId}/emotions",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(emotionService,times(0))
                .toggleRecipeInfoEmotion(any(Long.class),any(Long.class),any(EmotionToggleReq.class));
    }

    private void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
