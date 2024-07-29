package com.example.foodking.ingredient.dto.response;

import com.example.foodking.ingredient.domain.Ingredient;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class IngredientFindRes {

    private String ingredientName;

    private String ingredientAmount;

    public static IngredientFindRes toDTO(Ingredient ingredient){
        return IngredientFindRes.builder()
                .ingredientName(ingredient.getIngredientName())
                .ingredientAmount(ingredient.getIngredientAmount())
                .build();
    }
}
