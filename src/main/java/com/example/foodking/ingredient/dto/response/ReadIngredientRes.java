package com.example.foodking.ingredient.dto.response;

import com.example.foodking.ingredient.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReadIngredientRes {

    private String ingredientName;

    private String ingredientAmount;

    public static ReadIngredientRes toDTO(Ingredient ingredient){
        return ReadIngredientRes.builder()
                .ingredientName(ingredient.getIngredientName())
                .ingredientAmount(ingredient.getIngredientAmount())
                .build();
    }
}
