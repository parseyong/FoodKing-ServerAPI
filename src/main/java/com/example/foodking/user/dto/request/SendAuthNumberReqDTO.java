package com.example.foodking.user.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendAuthNumberReqDTO {

    @NotBlank(message = "전화번호를 입력하세요")
    private String phoneNum;

    // 테스트를 위한 생성자
    public SendAuthNumberReqDTO(String phoneNum){
        this.phoneNum=phoneNum;
    }
}
