package com.example.foodking.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoReqDTO {

    private String oldPassword;
    private String newPassword;
    private String nickName;
    private String phoneNum;
}
