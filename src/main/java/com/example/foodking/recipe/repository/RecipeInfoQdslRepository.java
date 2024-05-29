package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface RecipeInfoQdslRepository {

    List<ReadRecipeInfoRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder);

    List<ReadRecipeInfoRes> findLikedRecipeInfoList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier);

    Long findLikedRecipeInfoCnt(BooleanBuilder builder);

    ReadRecipeInfoRes findRecipeInfo(Long recipeinfoId);
}
