package com.example.foodking.recipe.repository;

import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeInfoPagingRepository {

    List<ReadRecipeInfoRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier, Pageable pageable);

    Long findRecipeInfoTotalCnt(BooleanBuilder builder);

    List<ReadRecipeInfoRes> findLikedRecipeInfoList(OrderSpecifier[] orderSpecifier, String searchKeyword, Pageable pageable, Long userId);

    Long findLikedRecipeInfoCnt(String searchKeyword, Long userId);
}
