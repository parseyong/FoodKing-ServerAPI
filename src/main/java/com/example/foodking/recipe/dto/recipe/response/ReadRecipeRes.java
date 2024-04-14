package com.example.foodking.recipe.dto.recipe.response;

import com.example.foodking.ingredient.dto.response.ReadIngredientRes;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipeWayInfo.dto.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.reply.dto.response.ReadReplyRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReadRecipeRes {

    private ReadRecipeInfoRes readRecipeInfoRes;
    private List<ReadIngredientRes> readIngredientResList;
    private List<ReadRecipeWayInfoResDTO> readRecipeWayInfoResDTOList;
    private List<ReadReplyRes> readReplyResList;
    private String recipeTip;
    private boolean isMyRecipe;
    private Long visitCnt;

}
