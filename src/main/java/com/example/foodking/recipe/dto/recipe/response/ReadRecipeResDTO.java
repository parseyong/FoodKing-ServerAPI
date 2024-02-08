package com.example.foodking.recipe.dto.recipe.response;

import com.example.foodking.recipe.dto.ingredient.response.ReadIngredientResDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.reply.dto.response.ReadReplyResDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
/*
    직렬화 과정에서 기본생성자가 없더라도 다른 생성자가 있다면 그 생성자를 이용하여 직렬화과정을 거친다
    그러나 Build를 Gradle로 하지않고 인텔리제이로 할 경우에는
    다른 생성자가 있더라도 기본생성자가 없으면 직렬화과정이 정상적으로 이루어지지 않는다.
*/
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadRecipeResDTO {

    private ReadRecipeInfoResDTO readRecipeInfoResDTO;
    private List<ReadIngredientResDTO> readIngredientResDTOList;
    private List<ReadRecipeWayInfoResDTO> readRecipeWayInfoResDTOList;
    private List<ReadReplyResDTO> readReplyResDTOList;
    private String recipeTip;
    private boolean isMyRecipe;

}
