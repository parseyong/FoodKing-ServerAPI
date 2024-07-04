package com.example.foodking.recipeWayInfo.dto.response;

import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 캐싱의 역직렬화를 위한 기본생성자.
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
