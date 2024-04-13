package com.example.foodking.ingredient.repository;

import com.example.foodking.ingredient.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient,Long> {

}
