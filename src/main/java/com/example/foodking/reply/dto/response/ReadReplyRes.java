package com.example.foodking.reply.dto.response;

import com.example.foodking.reply.domain.Reply;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class ReadReplyRes {

    private Long replyId;

    @JsonProperty("isMyReply")
    @Getter(AccessLevel.NONE)
    private boolean isMyReply;

    private String content;

    private String writerNickname;

    private Long emotionCnt;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    public static ReadReplyRes toDTO(Reply reply, String writerNickname , boolean isMyReply){
        return ReadReplyRes.builder()
                .content(reply.getContent())
                .writerNickname(writerNickname)
                .replyId(reply.getReplyId())
                .isMyReply(isMyReply)
                .emotionCnt(reply.getLikeCnt())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }
}
