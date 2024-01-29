package com.example.foodking.CoolSms;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.User.CoolSmsService;
import com.example.foodking.User.DTO.Request.PhoneAuthReqDTO;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private CoolSmsService coolSmsService;
    @Mock
    private Message coolsms;
    private String testPhoneNum = "01011111111";


    @Test
    @DisplayName("인증문자 보내기테스트 -> (성공)")
    public void sendMessageSuccess() throws CoolsmsException {
        //given
        given(coolsms.send(any(HashMap.class))).willReturn(new JSONObject());

        //when
        String authenticationNumber = coolSmsService.sendMessage(testPhoneNum);

        //then
        assertThat(coolSmsService.getAuthenticationNumberMap().get(testPhoneNum)).isEqualTo(authenticationNumber);
        verify(coolsms,times(1)).send(any(HashMap.class));
    }

    @Test
    @DisplayName("인증문자 보내기테스트 -> (실패 : coolsms 내부 문제)")
    public void sendMessageFail1() throws CoolsmsException {
        //given
        given(coolsms.send(any(HashMap.class))).willThrow(CoolsmsException.class);

        // when, then
        try{
            String authenticationNumber = coolSmsService.sendMessage(testPhoneNum);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.COOLSMS_EXCEPTION);
            verify(coolsms,times(1)).send(any(HashMap.class));
        }
    }

    @Test
    @DisplayName("인증문자 보내기테스트 -> (실패 : 전화번호 형식 예외)")
    public void sendMessageFail2() throws CoolsmsException {
        //given

        // when, then
        try{
            String authenticationNumber = coolSmsService.sendMessage("전화번호아님");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_PHONENUM);
            verify(coolsms,times(0)).send(any(HashMap.class));
        }
    }

    @Test
    @DisplayName("인증번호 확인 테스트 -> (성공)")
    public void authNumCheckSuccess(){
        //given
        coolSmsService.getAuthenticationNumberMap().put(testPhoneNum,"1111");
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum(testPhoneNum)
                .authenticationNumber("1111")
                .build();

        //when
        coolSmsService.authNumCheck(phoneAuthReqDTO);

        //then
        assertThat(coolSmsService.getAuthenticationedPhoneNumSet().contains(testPhoneNum));
    }
    @Test
    @DisplayName("인증번호 확인 테스트 -> (실패 : 인증번호 불일치)")
    public void authNumCheckFail1(){
        //given
        coolSmsService.getAuthenticationNumberMap().put(testPhoneNum,"1111");
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum(testPhoneNum)
                .authenticationNumber("1112")
                .build();

        //when, then
        try{
            coolSmsService.authNumCheck(phoneAuthReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SMS_AUTHENTICATION_FAIL);
        }
    }

    @Test
    @DisplayName("인증된 번호인지 테스트 -> (성공)")
    public void isAuthenticatedNumSuccess(){
        //given
        coolSmsService.getAuthenticationedPhoneNumSet().add(testPhoneNum);
        //when
        boolean isSuccess = coolSmsService.isAuthenticatedNum(testPhoneNum);
        //then
        assertThat(isSuccess).isTrue();
    }

    @Test
    @DisplayName("인증된 번호인지 테스트 -> (실패 : 인증되지 않은 번호)")
    public void isAuthenticatedNumFail1(){
        //given

        //when, then
        try{
            boolean isSuccess = coolSmsService.isAuthenticatedNum(testPhoneNum);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.SMS_NOT_AUTHENTICATION);
        }
    }

    @Test
    @DisplayName("인증정보 삭제테스트 -> (성공)")
    public void deleteAuthInfoSuccess(){
        //given
        coolSmsService.getAuthenticationedPhoneNumSet().add(testPhoneNum);
        coolSmsService.getAuthenticationNumberMap().put(testPhoneNum,"1111");

        //when
        coolSmsService.deleteAuthInfo(testPhoneNum);

        //then
        assertThat(coolSmsService.getAuthenticationedPhoneNumSet().contains(testPhoneNum)).isFalse();
        assertThat(coolSmsService.getAuthenticationNumberMap().containsKey(testPhoneNum)).isFalse();
    }

}
