package com.example.foodking.RecipeInfo.DTO;

import com.example.foodking.Ingredient.DTO.AddIngredientReqDTO;
import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.RecipeInfo.RecipeInfo;
import com.example.foodking.RecipeInfo.RecipeInfoType;
import com.example.foodking.RecipeWayInfo.DTO.AddRecipeWayInfoReqDTO;
import com.example.foodking.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    private List<@Valid AddIngredientReqDTO> addIngredientReqDTOList;
    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid AddRecipeWayInfoReqDTO> addRecipeWayInfoReqDTOList;

    public static RecipeInfo toRecipeInfoEntity(AddRecipeReqDTO addRecipeReqDTO, User user){
        return RecipeInfo.builder()
                .calogy(addRecipeReqDTO.getCalogy())
                .cookingTime(addRecipeReqDTO.getCookingTime())
                .ingredientCost(addRecipeReqDTO.getIngredentCost())
                .recipeTip(addRecipeReqDTO.getRecipeTip())
                .recipeName(addRecipeReqDTO.getRecipeName())
                .recipeInfoType(addRecipeReqDTO.getRecipeInfoType())
                .user(user)
                .build();
    }

    public static List<Ingredient> toIngredientListEntity(List<AddIngredientReqDTO> addIngredientReqDTOList,
                                                          RecipeInfo recipeInfo){
        List<Ingredient> ingredientList = new ArrayList<>();
        for( AddIngredientReqDTO addIngredientReqDTO : addIngredientReqDTOList){
            ingredientList.add(AddIngredientReqDTO.toEntity(addIngredientReqDTO,recipeInfo));
        }

        return ingredientList;
    }

    public static List<RecipeWayInfo> toRecipeWayInfoListEntity(List<AddRecipeWayInfoReqDTO> addRecipeWayInfoReqDTOList,
                                                                RecipeInfo recipeInfo){
        List<RecipeWayInfo> recipeWayInfoList = new ArrayList<>();
        for( AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO : addRecipeWayInfoReqDTOList){
            recipeWayInfoList.add(AddRecipeWayInfoReqDTO.toEntity(addRecipeWayInfoReqDTO,recipeInfo));
        }

        return recipeWayInfoList;
    }
}
