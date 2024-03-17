package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient,Long>{

}
