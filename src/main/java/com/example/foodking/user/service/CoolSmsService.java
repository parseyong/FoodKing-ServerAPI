package com.example.foodking.user.service;

import com.example.foodking.common.RedisPrefix;
import com.example.foodking.exception.CommondException;
import com.example.foodking.user.dto.request.AuthNumberCheckReq;
import com.example.foodking.user.dto.request.MessageSendReq;
import com.example.foodking.util.AuthNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.foodking.exception.ExceptionCode.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class CoolSmsService {

    @Value("${CoolSMS.Api.Key}")
    private String apiKey; // 발급받은 api_key

    @Value("${CoolSMS.Api.Secret}")
    private String apiSecret; // 발급받은 api_secret

    @Value("${CoolSMS.Caller}")
    private String caller; // 발신자 번호

    private Message coolSms;

    @Qualifier("authRedis")
    private final RedisTemplate<String,String> authRedis;

    @PostConstruct
    protected void init() {
        coolSms = new Message(apiKey,apiSecret);
    }

    // 입력된 전화번호에 인증번호를 발급한다.
    @Transactional
    public String sendMessage(MessageSendReq messageSendReq){
        String phoneNum = messageSendReq.getPhoneNum();

        if(phoneNum.length() > 12 || phoneNum.length() < 8 || !Pattern.matches("\\d+", phoneNum))
            throw new CommondException(NOT_PHONENUM_TYPE);

        int authenticationNumber = AuthNumberGenerator.createAuthNumber();

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNum);    // 수신전화번호
        params.put("from", caller);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "sms");     // 타입
        params.put("text", "FoodKing 휴대폰인증 메시지 : 인증번호는" + "["+authenticationNumber+"]" + "입니다.");

        try {
            coolSms.send(params);
            //인증번호 확인을 위해 발급된 인증번호를 key-value(전화번호-인증번호)형태로 저장, 60초 후 자동 소멸
            authRedis.opsForValue().set(
                    RedisPrefix.AUTH_NUM_REDIS + phoneNum,
                    String.valueOf(authenticationNumber),
                    60,
                    TimeUnit.SECONDS);

        } catch (CoolsmsException e) {
            System.out.println(e.getCode()+":"+e.getMessage());
            log.error(e.getCode()+":"+e.getMessage());
            throw new CommondException(COOLSMS_EXCEPTION);
        }
        return String.valueOf(authenticationNumber);
    }

    @Transactional
    //해당 전화번호에 발급된 인증번호에 대한 인증을 실행하는 메소드
    public void checkAuthNum(AuthNumberCheckReq authNumberCheckReq) {

        String authenticationNum = authRedis.opsForValue()
                .get(RedisPrefix.AUTH_NUM_REDIS + authNumberCheckReq.getPhoneNum());

        if(authenticationNum == null || !authenticationNum.equals(authNumberCheckReq.getAuthNumber()))
            throw new CommondException(SMS_AUTHENTICATION_FAIL);

        authRedis.opsForValue().set(
                RedisPrefix.IS_AUTH_NUM_REDIS + authNumberCheckReq.getPhoneNum(),
                "true",
                10,
                TimeUnit.MINUTES);
    }

    // 해당 전화번호가 인증이 완료된 전화번호인지 체크
    public boolean isAuthenticatedNum(String phoneNum){
        String isAuth = authRedis.opsForValue().get(RedisPrefix.IS_AUTH_NUM_REDIS + phoneNum);

        if(isAuth == null){
            throw new CommondException(SMS_NOT_AUTHENTICATION);
        }
        return true;
    }

    @Transactional
    public void deleteAuthInfo(String phoneNum){
        authRedis.delete(RedisPrefix.AUTH_NUM_REDIS + phoneNum);
        authRedis.delete(RedisPrefix.IS_AUTH_NUM_REDIS + phoneNum);
    }
}