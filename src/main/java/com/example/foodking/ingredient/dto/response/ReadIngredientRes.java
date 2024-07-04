package com.example.foodking.ingredient.dto.response;

import com.example.foodking.ingredient.domain.Ingredient;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
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
