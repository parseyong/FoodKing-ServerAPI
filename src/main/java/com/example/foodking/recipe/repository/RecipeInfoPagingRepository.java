package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeInfoPagingRepository {

    List<ReadRecipeInfoResDTO> findRecipeInfoPagingByCondition
            (Pageable pageable, OrderSpecifier[] createOrderSpecifier, BooleanBuilder builder, Object condition);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder,Object condition);
}
