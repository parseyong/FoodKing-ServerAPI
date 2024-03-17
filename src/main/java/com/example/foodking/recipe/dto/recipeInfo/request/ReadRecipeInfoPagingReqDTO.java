package com.example.foodking.recipe.dto.recipeInfo.request;

import com.example.foodking.recipe.common.RecipeSortType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadRecipeInfoPagingReqDTO {

    @NotNull(message ="정렬타입을 입력해주세요")
    private RecipeSortType recipeSortType;

    private String searchKeyword;

    private Object condition;

    public void addFindCondition(Object condition){
        this.condition=condition;
    }
}
