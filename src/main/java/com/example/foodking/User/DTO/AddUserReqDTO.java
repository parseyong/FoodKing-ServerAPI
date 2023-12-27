package com.example.foodking.User.DTO;

import com.example.foodking.User.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUserReqDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다")
    @NotBlank(message = "이메일 정보를 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Setter
    private String password;
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String passwordRepeat;
    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickName;
    @NotBlank(message = "전화번호를 입력해주세요")
    private String phoneNum;

    public static User toEntity(AddUserReqDTO addUserReqDTO){
        return User.builder()
                .email(addUserReqDTO.getEmail())
                .nickName(addUserReqDTO.getNickName())
                .password(addUserReqDTO.getPassword())
                .phoneNum(addUserReqDTO.getPhoneNum())
                .build();
    }

}
