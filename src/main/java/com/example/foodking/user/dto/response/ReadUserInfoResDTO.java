package com.example.foodking.user.dto.response;

import com.example.foodking.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReadUserInfoResDTO {

    private String email;

    private String nickName;

    private String phoneNum;

    public static ReadUserInfoResDTO toDTO(User user){
        return ReadUserInfoResDTO.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNum(user.getPhoneNum())
                .build();
    }
}
