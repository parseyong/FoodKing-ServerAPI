package com.example.foodking.RecipeInfo.DTO;

import com.example.foodking.Ingredient.DTO.AddIngredientReqDTO;
import com.example.foodking.RecipeInfo.RecipeInfoType;
import com.example.foodking.RecipeWayInfo.DTO.AddRecipeWayInfoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddRecipeReqDTO {

    @NotBlank(message = "레시피 이름을 입력해주세요")
    private String recipeName;
    @NotNull(message = "레시피 타입을 입력해주세요")
    private RecipeInfoType recipeInfoType;
    @NotNull(message = "재료비를 입력해주세요")
    private Long ingredentCost;
    @NotNull(message = "예상 조리시간을 입력해주세요")
    private Long cookingTime;
    @NotNull(message = "칼로리를 입력해주세요")
    private Long calogy;
    @NotBlank(message = "레시피 팁을 입력해주세요")
    private String recipeTip;
    @NotEmpty(message = "재료를 추가해주세요")
    private List<@Valid AddIngredientReqDTO> ingredentList;
    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid AddRecipeWayInfoReqDTO> recipeWayInfoList;
}
