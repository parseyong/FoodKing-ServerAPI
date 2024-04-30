package com.example.foodking.reply;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.reply.controller.ReplyController;
import com.example.foodking.reply.service.ReplyService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ReplyController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private ReplyService replyService;
    @MockBean
    private JwtProvider jwtProvider;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addReplySuccess() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("댓글 등록완료"))
                .andDo(print());

        verify(replyService,times(1)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : 인증실패)")
    public void addReplyFail1() throws Exception {
        //given
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
        String requestBody = "{\n" +
                "    \"content\" : \"\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.content").value("댓글내용을 입력해주세요"))
                .andDo(print());

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : pathVariable값 공벡)")
    public void addReplyFail3() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."));

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> (실패 : pathVariable값 타입예외)")
    public void addReplyFail4() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."));

        verify(replyService,times(0)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addReplyFail5() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(replyService).addReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(replyService).addReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(post("/{recipeInfoId}/replys",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(replyService,times(1)).addReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> 성공")
    public void updateReplySuccess() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수정완료"))
                .andDo(print());

        verify(replyService,times(1)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 인증실패)")
    public void updateReplyFail1() throws Exception {
        //given
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(replyService,times(0)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 입력값 공백)")
    public void updateReplyFail2() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.content").value("댓글내용을 입력해주세요"))
                .andDo(print());

        verify(replyService,times(0)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : PathVariable값 공백)")
    public void updateReplyFail3() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."));

        verify(replyService,times(0)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : PathVariable값 타입예외)")
    public void updateReplyFail4() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("replyId이 Long타입이여야 합니다."));

        verify(replyService,times(0)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void updateReplyFail5() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_REPLY)).when(replyService).updateReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 댓글입니다"));

        verify(replyService,times(1)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 댓글 수정권한 없음)")
    public void updateReplyFail6() throws Exception {
        //given
        makeAuthentication();
        String requestBody = "{\n" +
                "    \"content\" : \"test\"\n" +
                "}";
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_REPLY)).when(replyService).updateReply(any(Long.class),any(Long.class),any(String.class));

        //when,then
        this.mockMvc.perform(patch("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 댓글에 대한 권한이 없습니다"));

        verify(replyService,times(1)).updateReply(any(Long.class),any(Long.class),any(String.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> 성공")
    public void deleteReplySuccess() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제완료"))
                .andDo(print());

        verify(replyService,times(1)).deleteReply(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 인증실패)")
    public void deleteReplyFail1() throws Exception {
        //given

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(replyService,times(0)).deleteReply(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : PathVariable값 공백)")
    public void deleteReplyFail2() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}"," ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."));

        verify(replyService,times(0)).deleteReply(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : PathVariable값 타입예외)")
    public void deleteReplyFail3() throws Exception {
        //given
        makeAuthentication();

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("replyId이 Long타입이여야 합니다."));

        verify(replyService,times(0)).deleteReply(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void deleteReplyFail4() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_REPLY)).when(replyService).deleteReply(any(Long.class),any(Long.class));

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 댓글입니다"));

        verify(replyService,times(1)).deleteReply(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 댓글 삭제권한 없음)")
    public void deleteReplyFail5() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_REPLY)).when(replyService).deleteReply(any(Long.class),any(Long.class));

        //when,then
        this.mockMvc.perform(delete("/replys/{replyId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 댓글에 대한 권한이 없습니다"));

        verify(replyService,times(1)).deleteReply(any(Long.class),any(Long.class));
    }

    private void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
