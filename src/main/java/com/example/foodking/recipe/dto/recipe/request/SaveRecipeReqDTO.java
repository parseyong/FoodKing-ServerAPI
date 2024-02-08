package com.example.foodking.recipe.dto.recipe.request;

import com.example.foodking.recipe.dto.ingredient.request.SaveIngredientReqDTO;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReqDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.request.SaveRecipeWayInfoReqDTO;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeReqDTO {

    @NotNull
    @Valid
    private SaveRecipeInfoReqDTO saveRecipeInfoReqDTO;

    @NotEmpty(message = "재료를 추가해주세요")
    private List<@Valid SaveIngredientReqDTO> saveIngredientReqDTOList;

    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;

}
