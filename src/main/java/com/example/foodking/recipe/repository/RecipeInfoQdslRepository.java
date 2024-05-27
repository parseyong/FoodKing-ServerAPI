package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface RecipeInfoQdslRepository {

    List<ReadRecipeInfoRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder);

    List<ReadRecipeInfoRes> findLikedRecipeInfoList(OrderSpecifier[] orderSpecifier, String searchKeyword, Long userId);

    Long findLikedRecipeInfoCnt(String searchKeyword, Long userId);

    ReadRecipeInfoRes findRecipeInfo(Long recipeinfoId);
}
