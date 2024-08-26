package com.example.foodking.recipe;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.request.RecipeInfoPagingFindReq;
import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.example.foodking.recipe.enums.RecipeInfoType;
import com.example.foodking.recipe.enums.RecipeSortType;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipePagingService;
import com.example.foodking.user.domain.User;
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
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipePagingServiceTest {

    @InjectMocks
    private RecipePagingService recipePagingService;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;
    private User user;
    private RecipeInfo recipeInfo;
    private RecipeInfoPagingFindReq recipeInfoPagingFindReq;

    @BeforeEach
    void beforeEach(){
        this.user= spy(User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build());

        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testName")
                .recipeWays(new ArrayList<>())
                .ingredients(new ArrayList<>())
                .calogy(1L)
                .build();

    }

    @Test
    @DisplayName("레시피 조건 페이징 조회 테스트 -> (성공)")
    public void readRecipeInfoPagingByConditionSuccess(){
        //given
        this.recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(RecipeSortType.LIKE)
                .userId(1L)
                .condition(RecipeInfoType.KOREAN)
                .build();

        RecipeInfoFindRes recipeInfoFindRes = RecipeInfoFindRes
                .toDTO(recipeInfo,1L,"writerNickName");

        given(recipeInfoRepository.findRecipeInfoTotalCnt(any())).willReturn(10L);
        given(recipeInfoRepository.findRecipeInfoPagingByCondition(any(),any()))
                .willReturn(List.of(recipeInfoFindRes));

        //when
        recipePagingService.findRecipeInfoPagingByCondition(recipeInfoPagingFindReq);

        //then
        verify(recipeInfoRepository,times(1)).findRecipeInfoTotalCnt(any());
        verify(recipeInfoRepository,times(1)).findRecipeInfoPagingByCondition(any(),any());
    }

    @Test
    @DisplayName("레시피 조건 페이징 조회 테스트 -> (실패 : 존재하지 않는 페이지)")
    public void readRecipeInfoPagingByConditionFail(){
        //given
        this.recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(RecipeSortType.LIKE)
                .userId(1L)
                .condition(RecipeInfoType.KOREAN)
                .build();

        given(recipeInfoRepository.findRecipeInfoTotalCnt(any())).willReturn(10L);
        given(recipeInfoRepository.findRecipeInfoPagingByCondition(any(),any()))
                .willReturn(List.of());

        //when, then
        try {
            recipePagingService.findRecipeInfoPagingByCondition(recipeInfoPagingFindReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_PAGE);
            verify(recipeInfoRepository,times(1)).findRecipeInfoTotalCnt(any());
            verify(recipeInfoRepository,times(1)).findRecipeInfoPagingByCondition(any(),any());
        }
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징 조회 테스트 -> (성공)")
    public void readLikedRecipeInfoPagingSuccess(){
        //given
        this.recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(RecipeSortType.LIKE)
                .userId(1L)
                .condition("like")
                .build();

        RecipeInfoFindRes recipeInfoFindRes = RecipeInfoFindRes
                .toDTO(recipeInfo,1L,"writerNickName");

        given(recipeInfoRepository.findLikedRecipeInfoCnt(any())).willReturn(10L);
        given(recipeInfoRepository.findLikedRecipeInfoPaging(any(),any()))
                .willReturn(List.of(recipeInfoFindRes));

        //when
        recipePagingService.findLikedRecipeInfoPaging(recipeInfoPagingFindReq);

        //then
        verify(recipeInfoRepository,times(1)).findLikedRecipeInfoCnt(any());
        verify(recipeInfoRepository,times(1)).findLikedRecipeInfoPaging(any(),any());
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징 조회 테스트 -> (실패 : 존재하지 않는 페이지)")
    public void readLikedRecipeInfoPagingFail(){
        //given
        this.recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(RecipeSortType.LIKE)
                .userId(1L)
                .condition("like")
                .build();

        given(recipeInfoRepository.findLikedRecipeInfoCnt(any())).willReturn(10L);
        given(recipeInfoRepository.findLikedRecipeInfoPaging(any(),any()))
                .willReturn(List.of());

        //when, then
        try {
            recipePagingService.findLikedRecipeInfoPaging(recipeInfoPagingFindReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_PAGE);
            verify(recipeInfoRepository,times(1)).findLikedRecipeInfoCnt(any());
            verify(recipeInfoRepository,times(1)).findLikedRecipeInfoPaging(any(),any());
        }
    }
}
