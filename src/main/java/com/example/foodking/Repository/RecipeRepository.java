package com.example.foodking.Repository;

import com.example.foodking.Domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface RecipeRepository extends JpaRepository<Recipe,Long>,
        QuerydslPredicateExecutor<Recipe> {

}