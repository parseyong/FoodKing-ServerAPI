package com.example.foodking.Recipe.RecipeWayInfo.DTO;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeWayInfoReqDTO {

    @NotBlank(message = "현재 순서의 조리법을 입력해주세요")
    private String recipeWay;
    @NotNull(message = "해당 조리법의 순서를 입력해주세요")
    private Long recipeOrder;

    public static RecipeWayInfo toEntity(SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO, RecipeInfo recipeInfo){
        return RecipeWayInfo.builder()
                .recipeWay(saveRecipeWayInfoReqDTO.getRecipeWay())
                .recipeOrder(saveRecipeWayInfoReqDTO.getRecipeOrder())
                .recipeInfo(recipeInfo)
                .build();
    }
}
