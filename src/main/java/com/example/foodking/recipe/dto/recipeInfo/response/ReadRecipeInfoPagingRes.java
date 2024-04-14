package com.example.foodking.recipe.dto.recipeInfo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReadRecipeInfoPagingRes {

    private Long totalRecipeCnt;
    private List<ReadRecipeInfoRes> readRecipeInfoResList;

    public static ReadRecipeInfoPagingRes toDTO(List<ReadRecipeInfoRes> readRecipeInfoResList, Long totalRecipeCnt){
        return ReadRecipeInfoPagingRes.builder()
                .readRecipeInfoResList(readRecipeInfoResList)
                .totalRecipeCnt(totalRecipeCnt)
                .build();
    }
}
