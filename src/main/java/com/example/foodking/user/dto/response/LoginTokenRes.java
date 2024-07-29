package com.example.foodking.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginTokenRes {

    String accessToken;
    String refreshToken;
}
