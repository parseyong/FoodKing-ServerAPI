package com.example.foodking.recipe.dto.recipeInfo.request;

import com.example.foodking.recipe.enums.RecipeSortType;
import lombok.*;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeInfoPagingFindReq {


    private RecipeSortType recipeSortType;

    private String searchKeyword;

    private Long userId;

    private Object condition;

    private Long lastId;

    private Object lastValue;

}
