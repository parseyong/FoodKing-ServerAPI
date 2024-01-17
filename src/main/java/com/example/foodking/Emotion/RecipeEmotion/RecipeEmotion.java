package com.example.foodking.Emotion.RecipeEmotion;

import com.example.foodking.Emotion.EmotionType;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
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
public class RecipeEmotion {

    @Id
    @Column(name = "recipe_emotion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeEmotionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "emotion_status")
    private EmotionType emotionType;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;
    @Builder
    public RecipeEmotion(EmotionType emotionType,User user, RecipeInfo recipeInfo){
        this.emotionType=emotionType;
        this.recipeInfo=recipeInfo;
        this.user=user;
    }
}
