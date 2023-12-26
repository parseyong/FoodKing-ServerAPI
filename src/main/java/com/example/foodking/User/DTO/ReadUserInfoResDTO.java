package com.example.foodking.User.DTO;

import com.example.foodking.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
