package com.example.foodking.Recipe.DTO.Request;

import com.example.foodking.Recipe.Ingredient.DTO.Request.SaveIngredientReqDTO;
import com.example.foodking.Recipe.RecipeInfo.DTO.Request.SaveRecipeInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.Request.SaveRecipeWayInfoReqDTO;
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
