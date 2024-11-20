package com.example.foodking.user.dto.response;

import com.example.foodking.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class UserFindRes {

    private final String email;

    private final String nickName;

    private final String phoneNum;

    public static UserFindRes toDTO(User user){
        return UserFindRes.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNum(user.getPhoneNum())
                .build();
    }
}
