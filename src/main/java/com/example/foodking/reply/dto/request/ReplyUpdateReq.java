package com.example.foodking.reply.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyUpdateReq {

    @NotBlank(message = "댓글내용을 입력해주세요")
    String content;
}
