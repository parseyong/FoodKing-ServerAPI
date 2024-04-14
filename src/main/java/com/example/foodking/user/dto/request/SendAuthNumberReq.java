package com.example.foodking.user.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/*
    생성자가 하나도 없으면 기본생성자를 컴파일과정에서 자동으로 생성해준다.(버전,빌드도구 등에 따라 차이는 있다.)
    내부적으로 자동생성이 되지만 코드의 가독성을 위해 @NoArgsConstructor를 명시해주었다.
*/
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendAuthNumberReq {

    @NotBlank(message = "전화번호를 입력하세요")
    private String phoneNum;

    // 테스트를 위한 생성자
    public SendAuthNumberReq(String phoneNum){
        this.phoneNum=phoneNum;
    }
}
