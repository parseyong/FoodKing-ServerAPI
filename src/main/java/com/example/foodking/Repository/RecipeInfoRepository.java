package com.example.foodking.Repository;

import com.example.foodking.Domain.RecipeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeInfoRepository extends JpaRepository<RecipeInfo,Long>,
        QuerydslPredicateExecutor<RecipeInfo> {

}
