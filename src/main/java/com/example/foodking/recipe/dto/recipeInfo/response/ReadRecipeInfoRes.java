package com.example.foodking.recipe.dto.recipeInfo.response;

import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadRecipeInfoRes {

    private String recipeName;
    private RecipeInfoType recipeInfoType;
    private Long ingredentCost;
    private Long cookingTime;
    private Long calogy;
    private Long recipeInfoId;
    private String recipeImageUrl;
    private Long replyCnt;
    private Long emotionCnt;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Long visitCnt;
    private Long writerUserId;
    private String writerNickName;

    @JsonIgnore
    private RecipeInfo recipeInfo;

    public static ReadRecipeInfoRes toDTO(RecipeInfo recipeInfo, Long writerUserId, String writerNickName){
        return ReadRecipeInfoRes.builder()
                .recipeInfo(recipeInfo)
                .calogy(recipeInfo.getCalogy())
                .recipeInfoType(recipeInfo.getRecipeInfoType())
                .recipeName(recipeInfo.getRecipeName())
                .cookingTime(recipeInfo.getCookingTime())
                .ingredentCost(recipeInfo.getIngredientCost())
                .recipeInfoId(recipeInfo.getRecipeInfoId())
                .recipeImageUrl(recipeInfo.getRecipeImage())
                .replyCnt((long) recipeInfo.getReplyList().size())
                .emotionCnt(recipeInfo.getLikeCnt())
                .regDate(recipeInfo.getRegDate())
                .modDate(recipeInfo.getModDate())
                .visitCnt(recipeInfo.getVisitCnt())
                .writerNickName(writerNickName)
                .writerUserId(writerUserId)
                .build();
    }
}
