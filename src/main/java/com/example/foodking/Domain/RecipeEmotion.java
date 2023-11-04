package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeEmotionId;

    @Column(nullable = false)
    private EmotionType emotionStatus;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;
    @Builder
    public RecipeEmotion(EmotionType emotionStatus,User user, RecipeInfo recipeInfo){
        this.emotionStatus=emotionStatus;
        this.recipeInfo=recipeInfo;
        this.user=user;
    }
}
