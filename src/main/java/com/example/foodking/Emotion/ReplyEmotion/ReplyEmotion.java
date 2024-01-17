package com.example.foodking.Emotion.ReplyEmotion;

import com.example.foodking.Emotion.EmotionType;
import com.example.foodking.Reply.Reply;
import com.example.foodking.User.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyEmotion {

    @Id
    @Column(name = "reply_emotion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyEmotionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "emotion_status")
    private EmotionType emotionType;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reply_id",nullable = false)
    private Reply reply;
    @Builder
    public ReplyEmotion(EmotionType emotionType, User user, Reply reply){
        this.emotionType=emotionType;
        this.reply=reply;
        this.user=user;
    }
}
