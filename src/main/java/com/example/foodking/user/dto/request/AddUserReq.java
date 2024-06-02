package com.example.foodking.user.dto.request;

import com.example.foodking.user.domain.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddUserReq {

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일 정보를 입력해주세요")
    private String email;
    @Setter
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String passwordRepeat;
    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickName;
    @NotBlank(message = "전화번호를 입력해주세요")
    private String phoneNum;

    public static User toEntity(AddUserReq addUserReq){
        return User.builder()
                .email(addUserReq.getEmail())
                .nickName(addUserReq.getNickName())
                .password(addUserReq.getPassword())
                .phoneNum(addUserReq.getPhoneNum())
                .build();
    }

}
