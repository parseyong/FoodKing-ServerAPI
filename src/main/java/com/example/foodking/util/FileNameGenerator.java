package com.example.foodking.util;

import java.util.UUID;

public class FileNameGenerator {

    public static String createFileName(String originalFileName){

        // 파일 이름으로 쓸 uuid 생성, 동일한 파일명이 들어왔을 때 파일명의 중복을 피하기위해 UUID를 사용.
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출(ex : .png)
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }
}
