package com.example.foodking.user.dto.response;

import com.example.foodking.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserReadRes {

    private String email;

    private String nickName;

    private String phoneNum;

    public static UserReadRes toDTO(User user){
        return UserReadRes.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNum(user.getPhoneNum())
                .build();
    }
}
