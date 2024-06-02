package com.example.foodking.emotion.repository;

import com.example.foodking.emotion.domain.RecipeEmotion;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeEmotionRepository extends CrudRepository<RecipeEmotion,Long> {

    Optional<RecipeEmotion> findByRecipeInfoAndUser(RecipeInfo recipeInfo, User user);
}
