package com.example.foodking.ingredient.repository;

import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.recipe.domain.RecipeInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IngredientRepository extends CrudRepository<Ingredient,Long> {

    List<Ingredient> findAllByRecipeInfo(RecipeInfo recipeInfo);
}
