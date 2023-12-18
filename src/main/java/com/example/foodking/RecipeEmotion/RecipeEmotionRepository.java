package com.example.foodking.RecipeEmotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeEmotionRepository extends JpaRepository<RecipeEmotion,Long>,
        QuerydslPredicateExecutor<RecipeEmotion> {

}
