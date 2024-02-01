package com.example.foodking.Recipe.Ingredient.DTO.Request;

import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveIngredientReqDTO {

    @NotBlank(message = "재료명을 입력해주세요")
    private String ingredientName;
    @NotBlank(message = "재료의 양을 입력해주세요")
    private String ingredientAmount;

    public static Ingredient toEntity(SaveIngredientReqDTO saveIngredientReqDTO, RecipeInfo recipeInfo){
        return Ingredient.builder()
                .ingredientName(saveIngredientReqDTO.getIngredientName())
                .ingredientAmount(saveIngredientReqDTO.getIngredientAmount())
                .recipeInfo(recipeInfo)
                .build();
    }

}
