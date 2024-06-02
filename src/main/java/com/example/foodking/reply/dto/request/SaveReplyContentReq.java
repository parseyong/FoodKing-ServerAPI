package com.example.foodking.reply.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveReplyContentReq {

    @NotBlank(message = "댓글내용을 입력해주세요")
    String content;
}
