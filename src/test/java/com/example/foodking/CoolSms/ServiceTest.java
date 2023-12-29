package com.example.foodking.CoolSms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private CoolSmsService coolSmsService;

    @Test
    @DisplayName("인증문자 보내기테스트 -> (성공)")
    public void sendMessage(){
        //given

        //when

        //then
    }

}
