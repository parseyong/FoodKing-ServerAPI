package com.example.foodking.user.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindPwdReqDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일 정보를 입력해주세요")
    private String email;

    @NotBlank(message = "전화번호를 입력해주세요")
    private String phoneNum;

}
