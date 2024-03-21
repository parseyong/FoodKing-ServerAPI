package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient,Long> {

}
