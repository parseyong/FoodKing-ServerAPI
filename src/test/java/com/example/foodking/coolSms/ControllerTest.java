package com.example.foodking.coolSms;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.controller.CoolSmsController;
import com.example.foodking.user.dto.request.AuthNumberCheckReq;
import com.example.foodking.user.dto.request.MessageSendReq;
import com.example.foodking.user.service.CoolSmsService;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CoolSmsController.class)
@Import(SecurityConfig.class)
public class ControllerTest {

    @MockBean
    private CoolSmsService coolSmsService;
    @Autowired
    private MockMvc mockMvc;
    /*
        JwtAuthenticationFilter클래스는 Filter이므로 @WebMvcTest에 스캔이 되지만 JwtProvider클래스는
        @Component로 선언되어있으므로 @WebMvcTest의 스캔대상이 아니다.
        따라서 JwtAuthenticationFilter클래스에서 JwtProvider 빈을 가져올 수 없어 테스트가 정상적으로 수행되지 않는다.
        따라서 JwtProvider를 Mock객체로 대체하여 해당 문제를 해결하였다.
    */
    @MockBean
    private JwtProvider jwtProvider;
    private Gson gson = new Gson();

    @Test
    @DisplayName("문자보내기 테스트 -> (성공)")
    public void sendMessageSuccess() throws Exception {
        //given
        MessageSendReq messageSendReq = MessageSendReq.builder()
                .phoneNum("01011111111")
                .build();
        String requestBody = gson.toJson(messageSendReq);

        //when,then
        this.mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증번호 전송"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(MessageSendReq.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : 전화번호 형식 예외)")
    public void sendMessageFail1() throws Exception {
        //given
        MessageSendReq messageSendReq = MessageSendReq.builder()
                .phoneNum("번호아님")
                .build();
        String requestBody = gson.toJson(messageSendReq);
        given(coolSmsService.sendMessage(any(MessageSendReq.class)))
                .willThrow(new CommondException(ExceptionCode.NOT_PHONENUM_TYPE));

        //when,then
        this.mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 전화번호 형식이 아닙니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("올바른 전화번호 형식이 아닙니다"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(MessageSendReq.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : Coolsms 내부 문제)")
    public void sendMessageFail2() throws Exception {
        //given
        MessageSendReq messageSendReq = MessageSendReq.builder()
                .phoneNum("01011111111")
                .build();
        String requestBody = gson.toJson(messageSendReq);
        given(coolSmsService.sendMessage(any(MessageSendReq.class)))
                .willThrow(new CommondException(ExceptionCode.COOLSMS_EXCEPTION));

        //when,then
        this.mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("인증시스템에 문제가 발생했습니다"))
                .andDo(print());

        verify(coolSmsService,times(1)).sendMessage(any(MessageSendReq.class));
    }

    @Test
    @DisplayName("문자보내기 테스트 -> (실패 : 입력값 공백)")
    public void sendMessageFail3() throws Exception {
        //given
        MessageSendReq messageSendReq = MessageSendReq.builder()
                .phoneNum("")
                .build();
        String requestBody = gson.toJson(messageSendReq);

        //when,then
        this.mockMvc.perform(post("/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력하세요"))
                .andDo(print());

        verify(coolSmsService,times(0)).sendMessage(any(MessageSendReq.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (성공)")
    public void authNumCheckSuccess() throws Exception {
        //given
        AuthNumberCheckReq authNumberCheckReq = AuthNumberCheckReq.builder()
                .phoneNum("01056962173")
                .authNumber("1234")
                .build();
        String requestBody = gson.toJson(authNumberCheckReq);

        //when ,then
        this.mockMvc.perform(post("/message/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증 성공!"))
                .andDo(print());
        verify(coolSmsService,times(1)).checkAuthNum(any(AuthNumberCheckReq.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (실패 : 입력값 공백)")
    public void authNumCheckFail1() throws Exception {
        //given
        AuthNumberCheckReq authNumberCheckReq = AuthNumberCheckReq.builder()
                .phoneNum("")
                .authNumber("")
                .build();
        String requestBody = gson.toJson(authNumberCheckReq);

        //when ,then
        this.mockMvc.perform(post("/message/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력하세요"))
                .andDo(print());
        verify(coolSmsService,times(0)).checkAuthNum(any(AuthNumberCheckReq.class));
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (실패 : 인증번호 불일치)")
    public void authNumCheckFail2() throws Exception {
        //given
        AuthNumberCheckReq authNumberCheckReq = AuthNumberCheckReq.builder()
                .phoneNum("01056962173")
                .authNumber("1234")
                .build();
        String requestBody = gson.toJson(authNumberCheckReq);
        doThrow(new CommondException(ExceptionCode.SMS_AUTHENTICATION_FAIL))
                .when(coolSmsService).checkAuthNum(any(AuthNumberCheckReq.class));


        //when ,then
        this.mockMvc.perform(post("/message/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다"))
                .andDo(print());
        verify(coolSmsService,times(1)).checkAuthNum(any(AuthNumberCheckReq.class));
    }
}
