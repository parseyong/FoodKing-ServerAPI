package com.example.foodking.RecipeWayInfo.DTO;

import com.example.foodking.RecipeInfo.RecipeInfo;
import com.example.foodking.RecipeWayInfo.RecipeWayInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddRecipeWayInfoReqDTO {

    @NotBlank(message = "현재 순서의 조리법을 입력해주세요")
    private String recipeWay;
    @NotNull(message = "해당 조리법의 순서를 입력해주세요")
    private Long recipeOrder;

    public static RecipeWayInfo toEntity(AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO, RecipeInfo recipeInfo){
        return RecipeWayInfo.builder()
                .recipeWay(addRecipeWayInfoReqDTO.getRecipeWay())
                .recipeOrder(addRecipeWayInfoReqDTO.getRecipeOrder())
                .recipeInfo(recipeInfo)
                .build();
    }
}
