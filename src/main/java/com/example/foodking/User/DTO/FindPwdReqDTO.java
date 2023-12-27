package com.example.foodking.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindPwdReqDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일 정보를 입력해주세요")
    private String email;

    @NotBlank(message = "전화번호를 입력해주세요")
    private String phoneNum;

    @NotBlank(message = "인증번호를 입력해주세요")
    private String authenticationNumber;
}
