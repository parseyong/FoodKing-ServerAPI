package com.example.foodking.recipeWayInfo;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import com.example.foodking.recipeWayInfo.dto.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipeWayInfo.repository.RecipeWayInfoRepository;
import com.example.foodking.recipeWayInfo.service.RecipeWayInfoService;
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
    private RecipeWayInfoService recipeWayInfoService;
    @Mock
    private RecipeWayInfoRepository recipeWayInfoRepository;
    private List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;

    @BeforeEach
    void beforeEach(){
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO1 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1 수정후")
                .build();
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO2 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2 수정후")
                .build();

        this.saveRecipeWayInfoReqDTOList = new ArrayList<>(List.of(saveRecipeWayInfoReqDTO1, saveRecipeWayInfoReqDTO2));
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
        recipeWayInfoService.addRecipeWay(saveRecipeWayInfoReqDTOList,recipeInfo);

        //then
        verify(recipeWayInfoRepository,times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법이 줄어든 경우 2개->0개 )")
    public void updateRecipeWayInfoSuccess(){
        //given
        RecipeWayInfo recipeWayInfo1 = RecipeWayInfo.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1")
                .build();
        RecipeWayInfo recipeWayInfo2 = RecipeWayInfo.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2")
                .build();

        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWayInfoList(new ArrayList<>(List.of(recipeWayInfo1,recipeWayInfo2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(2L);
        recipeWayInfoService.updateRecipeWayInfoList(new ArrayList<>(),recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(0L);
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법 수가 그대로인 경우 )")
    public void updateRecipeWayInfoSuccess2(){
        //given
        RecipeWayInfo recipeWayInfo1 = RecipeWayInfo.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1 수정전")
                .build();
        RecipeWayInfo recipeWayInfo2 = RecipeWayInfo.builder()
                .recipeOrder(2L)
                .recipeWay("조리법2 수정전")
                .build();

        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWayInfoList(new ArrayList<>(List.of(recipeWayInfo1,recipeWayInfo2)))
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(2L);
        recipeWayInfoService.updateRecipeWayInfoList(saveRecipeWayInfoReqDTOList, recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(2L);
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeWay()).isEqualTo("조리법1 수정후");
        assertThat(recipeInfo.getRecipeWayInfoList().get(1).getRecipeWay()).isEqualTo("조리법2 수정후");
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 조리법이 늘어난 경우 0개->2개 )")
    public void updateRecipeWayInfoSuccess3(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .recipeName("testName")
                .recipeWayInfoList(new ArrayList<>())
                .calogy(1L)
                .build();

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(0L);
        recipeWayInfoService.updateRecipeWayInfoList(saveRecipeWayInfoReqDTOList,recipeInfo);

        //then
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(2L);
    }

}
