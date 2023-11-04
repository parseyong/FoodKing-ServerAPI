package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyEmotionId;

    @Column(nullable = false)
    private EmotionType emotionStatus;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reply_id",nullable = false)
    private Reply reply;
    @Builder
    public ReplyEmotion(EmotionType emotionStatus, User user, Reply reply){
        this.emotionStatus=emotionStatus;
        this.reply=reply;
        this.user=user;
    }
}
