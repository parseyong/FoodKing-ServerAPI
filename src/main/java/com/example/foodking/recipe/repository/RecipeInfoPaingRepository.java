package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface RecipeInfoPaingRepository {

    List<RecipeInfoFindRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifiers);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder);

    List<RecipeInfoFindRes> findLikedRecipeInfoPaging(BooleanBuilder builder, OrderSpecifier[] orderSpecifiers);

    Long findLikedRecipeInfoCnt(BooleanBuilder builder);

    RecipeInfoFindRes findRecipeInfo(Long recipeinfoId);
}
