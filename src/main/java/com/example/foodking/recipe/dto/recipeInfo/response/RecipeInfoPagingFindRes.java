package com.example.foodking.recipe.dto.recipeInfo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RecipeInfoPagingFindRes {

    private final Long totalRecipeCnt;
    private final List<RecipeInfoFindRes> recipeInfoFindResList;

    public static RecipeInfoPagingFindRes toDTO(List<RecipeInfoFindRes> recipeInfoFindResList, Long totalRecipeCnt){
        return RecipeInfoPagingFindRes.builder()
                .recipeInfoFindResList(recipeInfoFindResList)
                .totalRecipeCnt(totalRecipeCnt)
                .build();
    }
}
