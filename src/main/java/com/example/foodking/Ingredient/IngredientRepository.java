package com.example.foodking.Ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IngredientRepository extends JpaRepository<Ingredient,Long>,
        QuerydslPredicateExecutor<Ingredient> {

}
