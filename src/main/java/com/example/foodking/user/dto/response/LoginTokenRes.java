package com.example.foodking.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public final class LoginTokenRes {

    private final String accessToken;
    private final String refreshToken;
}
