package com.example.foodking.recipe.dto.recipe.response;

import com.example.foodking.ingredient.dto.response.IngredientFindRes;
import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.example.foodking.recipeWay.dto.response.RecipeWayFindRes;
import com.example.foodking.reply.dto.response.ReplyFindRes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class RecipeFindRes {

    private RecipeInfoFindRes recipeInfoFindRes;
    private List<IngredientFindRes> ingredientFindResList;
    private List<RecipeWayFindRes> recipeWayFindResList;
    private List<ReplyFindRes> replyFindResList;

    @JsonProperty("isMyRecipe")
    @Getter(AccessLevel.NONE)
    private boolean isMyRecipe;

}
