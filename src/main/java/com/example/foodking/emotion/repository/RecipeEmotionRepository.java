package com.example.foodking.emotion.repository;

import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.domain.RecipeEmotion;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface RecipeEmotionRepository extends JpaRepository<RecipeEmotion,Long>,
        QuerydslPredicateExecutor<RecipeEmotion> {

    Optional<RecipeEmotion> findByRecipeInfoAndUser(RecipeInfo recipeInfo, User user);

    Long countByRecipeInfoAndEmotionType(RecipeInfo recipeInfo, EmotionType emotionType);
}
