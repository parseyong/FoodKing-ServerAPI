package com.example.foodking.CoolSms;

import com.example.foodking.CoolSms.DTO.PhoneAuthReqDTO;
import com.example.foodking.Exception.CommondException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Pattern;

import static com.example.foodking.Exception.ExceptionCode.*;

@Service
@Log4j2
public class CoolSmsService {

    @Value("${CoolSMS.Api.Key}")
    private String apiKey; // 발급받은 api_key
    @Value("${CoolSMS.Api.Secret}")
    private String apiSecret; // 발급받은 api_secret
    @Value("${CoolSMS.Caller}")
    private String callId; // 발신자 번호
    private Message coolSms;
    /*
        전화번호로 인증번호를 보내면 (전화번호-인증번호)를 key-value형태로 Map에 저장
        그 뒤 인증번호 확인 요청이 오면 이 Map을 통해 인증번호가 올바른지 확인한다.
        인증번호 확인이 되면 authenticationedPhoneNumSet에 저장하고 Map에서 해당 정보를 삭제한다.

        authenticationNumberMap : 전화번호와 인증번호를 저장한 Map
        authenticationedPhoneNumSet : 인증확인이 완료된 전화번호 set
    */
    @Getter
    private HashMap<String,String> authenticationNumberMap = new HashMap<>();
    @Getter
    private HashSet<String> authenticationedPhoneNumSet = new HashSet<>();

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
            //인증번호 확인을 위해 발급된 인증번호를 key-value(전화번호-인증번호)형태로 저장
            authenticationNumberMap.put(phoneNum, String.valueOf(authenticationNumber));
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

        String authenticationNum = authenticationNumberMap.get(phoneAuthReqDTO.getPhoneNum());
        log.info(authenticationNumberMap.toString() + "authNumcheck");
        log.info(authenticationedPhoneNumSet.toString() + "authNumcheck");

        if(authenticationNum == null || !authenticationNum.equals(phoneAuthReqDTO.getAuthenticationNumber()))
            throw new CommondException(SMS_AUTHENTICATION_FAIL);

        authenticationedPhoneNumSet.add(phoneAuthReqDTO.getPhoneNum());
    }
    
    // 해당 전화번호가 인증이 완료된 전화번호인지 체크 즉, authenticationedPhoneNumset에 전화번호가 존재하는지 체크한다.
    public boolean isAuthenticatedNum(String phoneNum){

        log.info(authenticationNumberMap.toString() + "isAuth");
        log.info(authenticationedPhoneNumSet.toString() + "isAuth");

        if(!authenticationedPhoneNumSet.contains(phoneNum)){
            throw new CommondException(SMS_NOT_AUTHENTICATION);
        }
        return true;
    }

    /*
        회원가입이 완료된 후 제일 마지막에 수행되는 메소드
        인증로직 완료 후 삭제해 버리면 로그인로직에서 예외 발생시 전화번호 인증을 다시해야하기때문에 회원가입이 완료된 후 삭제한다.
    */
    public void deleteAuthInfo(String phoneNum){
        authenticationedPhoneNumSet.remove(phoneNum);
        authenticationNumberMap.remove(phoneNum);
    }
}