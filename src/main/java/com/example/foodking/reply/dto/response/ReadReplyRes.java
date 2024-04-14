package com.example.foodking.reply.dto.response;

import com.example.foodking.reply.domain.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadReplyRes {

    private Long replyId;

    private boolean isMyReply;

    private String content;

    private String writerNickname;

    private Long emotionCnt;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    public static ReadReplyRes toDTO(Reply reply, String writerNickname , boolean isMyReply, Long emotionCnt){
        return ReadReplyRes.builder()
                .content(reply.getContent())
                .writerNickname(writerNickname)
                .replyId(reply.getReplyId())
                .isMyReply(isMyReply)
                .emotionCnt(emotionCnt)
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .build();
    }
}
