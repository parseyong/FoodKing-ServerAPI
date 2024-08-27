package com.example.foodking.recipe.dto.recipeInfo.response;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.enums.RecipeInfoType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class RecipeInfoFindRes {

    private String recipeName;
    private RecipeInfoType recipeInfoType;
    private Long ingredentCost;
    private Long cookingTime;
    private Long calogy;
    private String recipeTip;
    private Long recipeInfoId;
    private String recipeImageUrl;
    private Long replyCnt;
    private Long emotionCnt;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Long visitCnt;
    private Long writerUserId;
    private String writerNickName;

    public static RecipeInfoFindRes toDTO(RecipeInfo recipeInfo, Long replyCnt, Long writerUserId, String writerNickName){

        return RecipeInfoFindRes.builder()
                .calogy(recipeInfo.getCalogy())
                .recipeInfoType(recipeInfo.getRecipeInfoType())
                .recipeName(recipeInfo.getRecipeName())
                .cookingTime(recipeInfo.getCookingTime())
                .ingredentCost(recipeInfo.getIngredientCost())
                .recipeTip(recipeInfo.getRecipeTip())
                .recipeInfoId(recipeInfo.getRecipeInfoId())
                .recipeImageUrl(recipeInfo.getRecipeImage())
                .replyCnt(replyCnt)
                .emotionCnt(recipeInfo.getLikeCnt())
                .regDate(recipeInfo.getRegDate())
                .modDate(recipeInfo.getModDate())
                .visitCnt(recipeInfo.getVisitCnt())
                .writerNickName(writerNickName)
                .writerUserId(writerUserId)
                .build();
    }
}
