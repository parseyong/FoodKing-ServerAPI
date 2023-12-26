package com.example.foodking.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindPwdReqDTO {

    private String email;
    private String phoneNum;
    private String authenticationNumber;
}
