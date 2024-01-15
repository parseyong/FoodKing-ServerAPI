package com.example.foodking.Reply;

import com.example.foodking.Auth.CustomUserDetailsService;
import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Config.SecurityConfig;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
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

@WebMvcTest(value = ReplyController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private ReplyService replyService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addReplySuccess() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글 테스트"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 등록완료"))
                .andDo(print());

        verify(replyService,times(1)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : 인증실패)")
    public void addReplyFail1() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글 테스트"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : 입력값 공백)")
    public void addReplyFail2() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.content").value("내용을 입력해주세요"))
                .andDo(print());

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : pathVariable값 공벡)")
    public void addReplyFail3() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글테스트"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."));

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : pathVariable값 타입예외)")
    public void addReplyFail4() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글테스트"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andExpect(jsonPath("$.data.fieldName").value("recipeInfoId"))
                .andExpect(jsonPath("$.data.requiredType").value("Long"));

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addReplyFail5() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(replyService).addReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글테스트"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(replyService,times(1)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록테스트 -> (실패 : 존재하지 않는 레시피)")
    public void addReplyFail6() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(replyService).addReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("content","댓글테스트"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(replyService,times(1)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1l,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
