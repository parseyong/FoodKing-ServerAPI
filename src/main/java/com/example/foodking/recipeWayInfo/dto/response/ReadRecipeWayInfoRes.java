package com.example.foodking.recipeWayInfo.dto.response;

import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReadRecipeWayInfoRes {

    private String recipeWay;
    private Long recipeOrder;

    public static ReadRecipeWayInfoRes toDTO(RecipeWayInfo recipeWayInfo){
        return ReadRecipeWayInfoRes.builder()
                .recipeWay(recipeWayInfo.getRecipeWay())
                .recipeOrder(recipeWayInfo.getRecipeOrder())
                .build();
    }

}
