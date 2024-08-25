package com.example.foodking.ingredient;

import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.ingredient.dto.request.IngredientAddReq;
import com.example.foodking.ingredient.repository.IngredientRepository;
import com.example.foodking.ingredient.service.IngredientService;
import com.example.foodking.recipe.domain.RecipeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private IngredientService ingredientService;
    @Mock
    private IngredientRepository ingredientRepository;
    private List<IngredientAddReq> ingredientAddReqList;

    @BeforeEach
    void beforeEach(){
        IngredientAddReq ingredientAddReq1 = IngredientAddReq.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료수량1")
                .build();
        IngredientAddReq ingredientAddReq2 = IngredientAddReq.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료수량2")
                .build();

        this.ingredientAddReqList = new ArrayList<>(List.of(ingredientAddReq1, ingredientAddReq2));
    }

    @Test
    @DisplayName("재료 추가테스트 -> (성공)")
    public void addIngredientSuccess(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .calogy(1L)
                .build();

        //when
        ingredientService.addIngredients(ingredientAddReqList,recipeInfo);

        //then
        verify(ingredientRepository,times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("재료 수정 테스트 -> (성공 : 재료가 늘어난 경우 0개->2개 )")
    public void updateIngredientSuccess(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .ingredients(new ArrayList<>())
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(0L);
        ingredientService.updateIngredients(ingredientAddReqList, recipeInfo);

        //then
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("재료 수정 테스트 -> (성공 : 재료가 줄어든 경우 2개->0개 )")
    public void updateIngredientSuccess2(){
        //given
        Ingredient ingredient1 = Ingredient.builder()
                .ingredientName("재료양1")
                .ingredientAmount("재료수량1")
                .build();
        Ingredient ingredient2 = Ingredient.builder()
                .ingredientName("재료양2")
                .ingredientAmount("재료수량2")
                .build();

        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .ingredients(new ArrayList<>(List.of(ingredient1,ingredient2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(2L);
        ingredientService.updateIngredients(new ArrayList<>(), recipeInfo);

        //then
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(0L);
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 재료 개수변화 없음 )")
    public void updateIngredientSuccess3(){
        //given
        Ingredient ingredient1 = Ingredient.builder()
                .ingredientName("수정 전 이름1")
                .ingredientAmount("수정 전 양1")
                .build();
        Ingredient ingredient2 = Ingredient.builder()
                .ingredientName("수정 전 이름2")
                .ingredientAmount("수정 전 양2")
                .build();
        
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .ingredients( new ArrayList<>(List.of(ingredient1,ingredient2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(2L);
        ingredientService.updateIngredients(ingredientAddReqList, recipeInfo);

        //then
        assertThat(recipeInfo.getIngredients().size()).isEqualTo(2L);
        assertThat(recipeInfo.getIngredients().get(0).getIngredientName()).isEqualTo("재료명1");
        assertThat(recipeInfo.getIngredients().get(1).getIngredientName()).isEqualTo("재료명2");
        assertThat(recipeInfo.getIngredients().get(0).getIngredientAmount()).isEqualTo("재료수량1");
        assertThat(recipeInfo.getIngredients().get(1).getIngredientAmount()).isEqualTo("재료수량2");
    }
}
