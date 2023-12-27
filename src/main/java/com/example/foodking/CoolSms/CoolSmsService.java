package com.example.foodking.CoolSms;

import com.example.foodking.CoolSms.DTO.PhoneAuthReqDTO;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import lombok.extern.log4j.Log4j2;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    /*
        전화번호로 인증번호를 보내면 (전화번호-인증번호)를 key-value형태로 Map에 저장
        그 뒤 인증번호 확인 요청이 오면 이 Map을 통해 인증번호가 올바른지 확인한다.
        인증번호 확인이 되면 authenticationedPhoneNumSet에 저장하고 Map에서 해당 정보를 삭제한다.

        authenticationNumberMap : 전화번호와 인증번호를 저장한 Map
        authenticationedPhoneNumSet : 인증확인이 완료된 전화번호 set
    */
    private HashMap<String,String> authenticationNumberMap = new HashMap<>();
    private HashSet<String> authenticationedPhoneNumSet = new HashSet<>();

    public void sendMessage(String phoneNum){

        if(phoneNum.length() > 12 || phoneNum.length() < 8 || !Pattern.matches("\\d+", phoneNum))
            throw new CommondException(NOT_PHONENUM);

        Random randomNum = new Random();
        int authenticationNumber = randomNum.nextInt(0,9999); // 인증번호 생성

        Message coolSms = new  Message(apiKey,apiSecret);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNum);    // 수신전화번호
        params.put("from", callId);    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "sms");     // 타입
        params.put("text", "FoodKing 휴대폰인증 메시지 : 인증번호는" + "["+authenticationNumber+"]" + "입니다.");

        try {
            coolSms.send(params);
            authenticationNumberMap.put(phoneNum, String.valueOf(authenticationNumber)); //인증번호 확인을 위한 저장.
        } catch (CoolsmsException e) {
            System.out.println(e.getCode()+":"+e.getMessage());
            log.error(e.getCode()+":"+e.getMessage());
            throw new CommondException(COOLSMS_EXCEPTION);
        }
    }
    public void authNumCheck(PhoneAuthReqDTO phoneAuthReqDTO) {

        String authenticationNum = authenticationNumberMap.get(phoneAuthReqDTO.getPhoneNum());

        if(authenticationNum == null || !authenticationNum.equals(phoneAuthReqDTO.getAuthenticationNumber()))
            throw new CommondException(SMS_AUTHENTICATION_FAIL);

        authenticationedPhoneNumSet.add(phoneAuthReqDTO.getPhoneNum());
    }
    public void isAuthenticatedNum(String phoneNum){

        if(!authenticationedPhoneNumSet.contains(phoneNum)){
            throw new CommondException(SMS_NOT_AUTHENTICATION);
        }

        authenticationedPhoneNumSet.remove(phoneNum);
        authenticationNumberMap.remove(phoneNum);
    }

}