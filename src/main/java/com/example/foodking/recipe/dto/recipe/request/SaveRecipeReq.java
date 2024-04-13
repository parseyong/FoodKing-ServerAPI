package com.example.foodking.recipe.dto.recipe.request;

import com.example.foodking.ingredient.dto.request.SaveIngredientReq;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReq;
import com.example.foodking.recipeWayInfo.dto.request.SaveRecipeWayInfoReqDTO;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveRecipeReq {

    @NotNull
    @Valid
    private SaveRecipeInfoReq saveRecipeInfoReq;

    @NotEmpty(message = "재료를 추가해주세요")
    private List<@Valid SaveIngredientReq> saveIngredientReqList;

    @NotEmpty(message = "조리법을 추가해주세요")
    private List<@Valid SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;

}
