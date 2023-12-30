package com.example.foodking.Ingredient.DTO;

import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.RecipeInfo.RecipeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddIngredientReqDTO {

    @NotBlank(message = "재료명을 입력해주세요")
    private String ingredientName;
    @NotBlank(message = "재료의 양을 입력해주세요")
    private String ingredientAmount;

    public static Ingredient toEntity(AddIngredientReqDTO addIngredientReqDTO, RecipeInfo recipeInfo){
        return Ingredient.builder()
                .ingredientName(addIngredientReqDTO.getIngredientName())
                .ingredientAmount(addIngredientReqDTO.getIngredientAmount())
                .recipeInfo(recipeInfo)
                .build();
    }
}
