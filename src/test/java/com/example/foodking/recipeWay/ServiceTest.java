package com.example.foodking.recipeWay;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWay.domain.RecipeWay;
import com.example.foodking.recipeWay.dto.request.RecipeWayAddReq;
import com.example.foodking.recipeWay.repository.RecipeWayRepository;
import com.example.foodking.recipeWay.service.RecipeWayService;
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
    private RecipeWayService recipeWayService;
    @Mock
    private RecipeWayRepository recipeWayRepository;
    private List<RecipeWayAddReq> recipeWayAddReqs;

    @BeforeEach
    void beforeEach(){
        RecipeWayAddReq recipeWayAddReq1 = RecipeWayAddReq.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1 수정후")
                .build();
        RecipeWayAddReq recipeWayAddReq2 = RecipeWayAddReq.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2 수정후")
                .build();

        this.recipeWayAddReqs = new ArrayList<>(List.of(recipeWayAddReq1, recipeWayAddReq2));
    }

    @Test
    @DisplayName("조리법 추가테스트 -> (성공)")
    public void addRecipeWayInfoSuccess(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .calogy(1L)
                .build();

        //when
        recipeWayService.addRecipeWays(recipeWayAddReqs,recipeInfo);

        //then
        verify(recipeWayRepository,times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법이 줄어든 경우 2개->0개 )")
    public void updateRecipeWayInfoSuccess(){
        //given
        RecipeWay recipeWay1 = RecipeWay.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1")
                .build();
        RecipeWay recipeWay2 = RecipeWay.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2")
                .build();

        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWays(new ArrayList<>(List.of(recipeWay1, recipeWay2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(2L);
        recipeWayService.updateRecipeWays(new ArrayList<>(),recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(0L);
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법 수가 그대로인 경우 )")
    public void updateRecipeWayInfoSuccess2(){
        //given
        RecipeWay recipeWay1 = RecipeWay.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1 수정전")
                .build();
        RecipeWay recipeWay2 = RecipeWay.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2 수정전")
                .build();

        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWays(new ArrayList<>(List.of(recipeWay1, recipeWay2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(2L);
        recipeWayService.updateRecipeWays(recipeWayAddReqs, recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(2L);
        assertThat(recipeInfo.getRecipeWays().get(0).getRecipeWay()).isEqualTo("조리법1 수정후");
        assertThat(recipeInfo.getRecipeWays().get(1).getRecipeWay()).isEqualTo("조리법2 수정후");
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법이 늘어난 경우 0개->2개 )")
    public void updateRecipeWayInfoSuccess3(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWays(new ArrayList<>())
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(0L);
        recipeWayService.updateRecipeWays(recipeWayAddReqs,recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWays().size()).isEqualTo(2L);
    }

}
