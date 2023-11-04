package com.example.foodking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeEmotion extends JpaRepository<RecipeEmotion,Long>,
        QuerydslPredicateExecutor<RecipeEmotion> {

}
