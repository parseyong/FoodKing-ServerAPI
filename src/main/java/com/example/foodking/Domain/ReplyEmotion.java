package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyEmotion {

    @Id
    @Column(name = "reply_emotion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyEmotionId;

    @Enumerated(EnumType.STRING)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(nullable = false, name = "emotion_status")
    private EmotionType emotionStatus;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reply_id",nullable = false)
    private Reply reply;
    @Builder
    public ReplyEmotion(EmotionType emotionStatus, User user, Reply reply){
        this.emotionStatus=emotionStatus;
        this.reply=reply;
        this.user=user;
    }
}
