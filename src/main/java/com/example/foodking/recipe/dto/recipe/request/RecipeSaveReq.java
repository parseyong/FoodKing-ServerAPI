package com.example.foodking.recipe.dto.recipe.request;

import com.example.foodking.ingredient.dto.request.IngredientAddReq;
import com.example.foodking.recipe.dto.recipeInfo.request.RecipeInfoSaveReq;
import com.example.foodking.recipeWay.dto.request.RecipeWayAddReq;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeSaveReq {


    @Valid
    @NotNull(message = "레시피 정보를 입력해주세요")
    private RecipeInfoSaveReq recipeInfoSaveReq;

    @NotEmpty(message = "재료를 추가해주세요")
    private List<@Valid IngredientAddReq> ingredientAddReqs;

    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid RecipeWayAddReq> recipeWayAddReqs;

}
