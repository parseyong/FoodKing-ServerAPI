package com.example.foodking.recipe.dto.recipeInfo.request;

import com.example.foodking.recipe.enums.RecipeSortType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
public class RecipeInfoPagingFindReq {

    private final RecipeSortType recipeSortType;

    private final String searchKeyword;

    private final Long userId;

    private final Object condition;

    private final Long lastId;

    private final Object lastValue;

}
