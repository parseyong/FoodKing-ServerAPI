package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.RecipeInfo;
import org.springframework.data.repository.CrudRepository;

public interface RecipeInfoRepository extends CrudRepository<RecipeInfo,Long>, RecipeInfoPaingRepository {

}
