package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IngredientRepository extends JpaRepository<Ingredient,Long>,
        QuerydslPredicateExecutor<Ingredient> {

}
