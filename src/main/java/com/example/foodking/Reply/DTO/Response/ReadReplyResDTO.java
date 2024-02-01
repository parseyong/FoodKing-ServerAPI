package com.example.foodking.Reply.DTO.Response;

import com.example.foodking.Reply.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
/*
    직렬화 과정에서 기본생성자가 없더라도 다른 생성자가 있다면 그 생성자를 이용하여 직렬화과정을 거친다
    그러나 Build를 Gradle로 하지않고 인텔리제이로 할 경우에는
    다른 생성자가 있더라도 기본생성자가 없으면 직렬화과정이 정상적으로 이루어지지 않는다.
*/
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadReplyResDTO {

    private Long replyId;

    private boolean isMyReply;

    private String content;

    private String writerNickname;

    private Long emotionCnt;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    public static ReadReplyResDTO toDTO(Reply reply,String writerNickname ,boolean isMyReply, Long emotionCnt){
        return ReadReplyResDTO.builder()
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
