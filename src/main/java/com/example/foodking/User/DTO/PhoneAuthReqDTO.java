package com.example.foodking.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneAuthReqDTO {

    @NotBlank(message = "전화번호를 입력하세요")
    private String phoneNum;
    @NotBlank(message = "인증번호를 입력하세요")
    private String authenticationNumber;
}
