package com.example.foodking.Repository;

import com.example.foodking.Domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IngredientRepository extends JpaRepository<Ingredient,Long>,
        QuerydslPredicateExecutor<Ingredient> {

}
