package com.example.foodking.user.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.user.dto.request.PhoneAuthReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.example.foodking.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class CoolSmsService {

    private final RedissonClient authNumberRedis;

    @Qualifier("isAuthNumberRedis")
    private final RedissonClient isAuthNumberRedis;

    @Value("${CoolSMS.Api.Key}")
    private String apiKey; // 발급받은 api_key
    @Value("${CoolSMS.Api.Secret}")
    private String apiSecret; // 발급받은 api_secret
    @Value("${CoolSMS.Caller}")
    private String callId; // 발신자 번호
    private Message coolSms;

    @PostConstruct
    protected void init() {
        coolSms = new  Message(apiKey,apiSecret);
    }

    // 입력된 전화번호에 인증번호를 발급한다.
    public String sendMessage(String phoneNum){

        if(phoneNum.length() > 12 || phoneNum.length() < 8 || !Pattern.matches("\\d+", phoneNum))
            throw new CommondException(NOT_PHONENUM);

        Random randomNum = new Random();
        int authenticationNumber = randomNum.nextInt(0,9999); // 인증번호 생성

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNum);    // 수신전화번호
        params.put("from", callId);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "sms");     // 타입
        params.put("text", "FoodKing 휴대폰인증 메시지 : 인증번호는" + "["+authenticationNumber+"]" + "입니다.");

        try {
            coolSms.send(params);
            //인증번호 확인을 위해 발급된 인증번호를 key-value(전화번호-인증번호)형태로 저장, 60초 후 자동 소멸
            RBucket<String> authNumber = authNumberRedis.getBucket(phoneNum);
            authNumber.set(String.valueOf(authenticationNumber), 60, TimeUnit.SECONDS);

        } catch (CoolsmsException e) {
            System.out.println(e.getCode()+":"+e.getMessage());
            log.error(e.getCode()+":"+e.getMessage());
            throw new CommondException(COOLSMS_EXCEPTION);
        }
        return String.valueOf(authenticationNumber);
    }

    /*
        해당 전화번호에 발급된 인증번호에 대한 인증을 실행하는 메소드
        인증에 성공하면 authenticationedPhoneNumset에 전화번호를 저장한다.
    */
    public void authNumCheck(PhoneAuthReqDTO phoneAuthReqDTO) {

        RBucket<String> bucket = authNumberRedis.getBucket(phoneAuthReqDTO.getPhoneNum());
        String authenticationNum = bucket.get();

        if(authenticationNum == null || !authenticationNum.equals(phoneAuthReqDTO.getAuthenticationNumber()))
            throw new CommondException(SMS_AUTHENTICATION_FAIL);

        RBucket<String> isAuthBucket = isAuthNumberRedis.getBucket(phoneAuthReqDTO.getPhoneNum());
        isAuthBucket.set("true",10,TimeUnit.MINUTES);
    }
    
    // 해당 전화번호가 인증이 완료된 전화번호인지 체크 즉, authenticationedPhoneNumset에 전화번호가 존재하는지 체크한다.
    public boolean isAuthenticatedNum(String phoneNum){
        RBucket<String> isAuthBucket = isAuthNumberRedis.getBucket(phoneNum);
        String isAuth = isAuthBucket.get();

        if(isAuth == null){
            throw new CommondException(SMS_NOT_AUTHENTICATION);
        }
        return true;
    }

    /*
        회원가입이 완료된 후 제일 마지막에 수행되는 메소드
        인증로직 완료 후 삭제해 버리면 로그인로직에서 예외 발생시 전화번호 인증을 다시해야하기때문에 회원가입이 완료된 후 삭제한다.
    */
    public void deleteAuthInfo(String phoneNum){
        authNumberRedis.getBucket(phoneNum).delete();
        isAuthNumberRedis.getBucket(phoneNum).delete();
    }
}