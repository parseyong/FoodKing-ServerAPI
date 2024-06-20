package com.example.foodking.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserInfoReq {
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String oldPassword;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String newPassword;

    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickName;

}
