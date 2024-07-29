package com.example.foodking.ingredient.dto.request;

import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IngredientAddReq {

    @NotBlank(message = "재료명을 입력해주세요")
    private String ingredientName;
    @NotBlank(message = "재료의 양을 입력해주세요")
    private String ingredientAmount;

    public static Ingredient toEntity(IngredientAddReq ingredientAddReq, RecipeInfo recipeInfo){
        return Ingredient.builder()
                .ingredientName(ingredientAddReq.getIngredientName())
                .ingredientAmount(ingredientAddReq.getIngredientAmount())
                .recipeInfo(recipeInfo)
                .build();
    }

}
