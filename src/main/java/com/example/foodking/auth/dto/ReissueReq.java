package com.example.foodking.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReissueReq {

    @NotBlank(message = "refreshToken값을 입력해주세요")
    String refreshToken;
}
