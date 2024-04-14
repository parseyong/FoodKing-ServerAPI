package com.example.foodking.recipeWayInfo.dto.response;

import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReadRecipeWayInfoResDTO {

    private String recipeWay;
    private Long recipeOrder;

    public static ReadRecipeWayInfoResDTO toDTO(RecipeWayInfo recipeWayInfo){
        return ReadRecipeWayInfoResDTO.builder()
                .recipeWay(recipeWayInfo.getRecipeWay())
                .recipeOrder(recipeWayInfo.getRecipeOrder())
                .build();
    }

}
