package com.example.foodking.recipeWay.dto.request;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWay.domain.RecipeWay;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeWayAddReq {

    @NotBlank(message = "현재 순서의 조리법을 입력해주세요")
    private String recipeWay;
    @NotNull(message = "해당 조리법의 순서를 입력해주세요")
    private Long recipeOrder;

    public static RecipeWay toEntity(RecipeWayAddReq recipeWayAddReq, RecipeInfo recipeInfo){
        return RecipeWay.builder()
                .recipeWay(recipeWayAddReq.getRecipeWay())
                .recipeOrder(recipeWayAddReq.getRecipeOrder())
                .recipeInfo(recipeInfo)
                .build();
    }
}
