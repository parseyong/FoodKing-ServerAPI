package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.RecipeWayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeWayInfoRepository extends JpaRepository<RecipeWayInfo,Long>,
        QuerydslPredicateExecutor<RecipeWayInfo> {

}