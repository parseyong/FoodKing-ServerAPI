package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.RecipeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeInfoRepository extends JpaRepository<RecipeInfo,Long>,
        QuerydslPredicateExecutor<RecipeInfo> {

}
