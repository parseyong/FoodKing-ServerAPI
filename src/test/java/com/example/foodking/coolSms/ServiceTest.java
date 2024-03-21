package com.example.foodking.coolSms;

import com.example.foodking.user.service.CoolSmsService;
import net.nurigo.java_sdk.api.Message;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private CoolSmsService coolSmsService;
    @Mock
    private Message message;


}
