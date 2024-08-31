package com.example.foodking.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageSendReq {

    @NotBlank(message = "전화번호를 입력하세요")
    String phoneNum;
}
