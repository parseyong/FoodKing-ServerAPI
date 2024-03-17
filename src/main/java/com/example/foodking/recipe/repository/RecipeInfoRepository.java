package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.domain.RecipeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeInfoRepository extends JpaRepository<RecipeInfo,Long>, RecipeInfoPagingRepository{

}
