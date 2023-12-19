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

    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Setter
    private String password;
    @NotBlank
    private String passwordRepeat;
    @NotBlank
    private String nickName;
    @NotBlank
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
