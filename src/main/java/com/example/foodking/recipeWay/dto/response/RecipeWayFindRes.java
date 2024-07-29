package com.example.foodking.recipeWay.dto.response;

import com.example.foodking.recipeWay.domain.RecipeWay;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
@AllArgsConstructor
public class RecipeWayFindRes {

    private String recipeWay;
    private Long recipeOrder;

    public static RecipeWayFindRes toDTO(RecipeWay recipeWay){
        return RecipeWayFindRes.builder()
                .recipeWay(recipeWay.getRecipeWay())
                .recipeOrder(recipeWay.getRecipeOrder())
                .build();
    }

}
