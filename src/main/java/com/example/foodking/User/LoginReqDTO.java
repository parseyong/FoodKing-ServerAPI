package com.example.foodking.User;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDTO {

    private String email;
    private String password;
}
