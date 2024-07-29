package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface RecipeInfoPaingRepository {

    List<RecipeInfoFindRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder);

    List<RecipeInfoFindRes> findLikedRecipeInfoList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier);

    Long findLikedRecipeInfoCnt(BooleanBuilder builder);

    RecipeInfoFindRes findRecipeInfo(Long recipeinfoId);
}
