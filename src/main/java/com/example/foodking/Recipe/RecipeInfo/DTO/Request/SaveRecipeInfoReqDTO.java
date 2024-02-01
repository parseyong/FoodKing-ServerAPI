package com.example.foodking.Recipe.RecipeInfo.DTO.Request;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import com.example.foodking.User.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeInfoReqDTO {

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

    public static RecipeInfo toEntity(SaveRecipeInfoReqDTO saveRecipeInfoReqDTO, User user){
        return RecipeInfo.builder()
                .calogy(saveRecipeInfoReqDTO.getCalogy())
                .cookingTime(saveRecipeInfoReqDTO.getCookingTime())
                .ingredientCost(saveRecipeInfoReqDTO.getIngredentCost())
                .recipeTip(saveRecipeInfoReqDTO.getRecipeTip())
                .recipeName(saveRecipeInfoReqDTO.getRecipeName())
                .recipeInfoType(saveRecipeInfoReqDTO.getRecipeInfoType())
                .user(user)
                .build();
    }
}
