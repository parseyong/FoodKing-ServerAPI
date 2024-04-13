package com.example.foodking.recipe.dto.recipeInfo.request;

import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.user.domain.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeInfoReq {

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

    public static RecipeInfo toEntity(SaveRecipeInfoReq saveRecipeInfoReq, User user){
        return RecipeInfo.builder()
                .calogy(saveRecipeInfoReq.getCalogy())
                .cookingTime(saveRecipeInfoReq.getCookingTime())
                .ingredientCost(saveRecipeInfoReq.getIngredentCost())
                .recipeTip(saveRecipeInfoReq.getRecipeTip())
                .recipeName(saveRecipeInfoReq.getRecipeName())
                .recipeInfoType(saveRecipeInfoReq.getRecipeInfoType())
                .user(user)
                .build();
    }
}
