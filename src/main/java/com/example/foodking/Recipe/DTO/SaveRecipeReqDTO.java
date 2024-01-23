package com.example.foodking.Recipe.DTO;

import com.example.foodking.Recipe.Ingredient.DTO.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.User.User;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeReqDTO {

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
    private List<@Valid SaveIngredientReqDTO> saveIngredientReqDTOList;
    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;

    public static RecipeInfo toRecipeInfoEntity(SaveRecipeReqDTO saveRecipeReqDTO, User user){
        return RecipeInfo.builder()
                .calogy(saveRecipeReqDTO.getCalogy())
                .cookingTime(saveRecipeReqDTO.getCookingTime())
                .ingredientCost(saveRecipeReqDTO.getIngredentCost())
                .recipeTip(saveRecipeReqDTO.getRecipeTip())
                .recipeName(saveRecipeReqDTO.getRecipeName())
                .recipeInfoType(saveRecipeReqDTO.getRecipeInfoType())
                .user(user)
                .build();
    }

    public static List<Ingredient> toIngredientListEntity(List<SaveIngredientReqDTO> saveIngredientReqDTOList,
                                                          RecipeInfo recipeInfo){
        return saveIngredientReqDTOList.stream()
                .map(dto -> SaveIngredientReqDTO.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());
    }

    public static List<RecipeWayInfo> toRecipeWayInfoListEntity(List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList,
                                                                RecipeInfo recipeInfo){
        return saveRecipeWayInfoReqDTOList.stream()
                .map(dto -> SaveRecipeWayInfoReqDTO.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());
    }
}
