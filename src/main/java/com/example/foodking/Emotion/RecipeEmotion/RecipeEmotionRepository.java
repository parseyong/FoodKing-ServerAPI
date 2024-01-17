package com.example.foodking.Emotion.RecipeEmotion;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface RecipeEmotionRepository extends JpaRepository<RecipeEmotion,Long>,
        QuerydslPredicateExecutor<RecipeEmotion> {

    Optional<RecipeEmotion> findByRecipeInfoAndUser(RecipeInfo recipeInfo, User user);

}
