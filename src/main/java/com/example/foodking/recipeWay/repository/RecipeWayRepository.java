package com.example.foodking.recipeWay.repository;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWay.domain.RecipeWay;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeWayRepository extends CrudRepository<RecipeWay,Long> {

    List<RecipeWay> findAllByRecipeInfo(RecipeInfo recipeInfo);
}