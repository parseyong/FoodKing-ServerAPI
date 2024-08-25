package com.example.foodking.util;

import java.util.Random;

public class AuthNumberGenerator {

    public static Integer createAuthNumber(){
        Random randomNum = new Random();
        return randomNum.nextInt(0,9999); // 인증번호 생성
    }
}
