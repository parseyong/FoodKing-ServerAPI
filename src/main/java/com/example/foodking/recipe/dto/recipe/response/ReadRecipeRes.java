package com.example.foodking.recipe.dto.recipe.response;

import com.example.foodking.ingredient.dto.response.ReadIngredientRes;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipeWayInfo.dto.response.ReadRecipeWayInfoRes;
import com.example.foodking.reply.dto.response.ReadReplyRes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class ReadRecipeRes {

    private ReadRecipeInfoRes readRecipeInfoRes;
    private List<ReadIngredientRes> readIngredientResList;
    private List<ReadRecipeWayInfoRes> readRecipeWayInfoResList;
    private List<ReadReplyRes> readReplyResList;

    @JsonProperty("isMyRecipe")
    @Getter(AccessLevel.NONE)
    private boolean isMyRecipe;

}
