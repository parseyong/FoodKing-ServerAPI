package com.example.foodking.recipe;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.service.IngredientService;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipe.request.RecipeSaveReq;
import com.example.foodking.recipe.dto.recipe.response.RecipeFindRes;
import com.example.foodking.recipe.dto.recipeInfo.request.RecipeInfoSaveReq;
import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipeCachingService;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.recipeWay.service.RecipeWayService;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private RecipeWayService recipeWayService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReplyService replyService;
    @Mock
    private RecipeCachingService recipeCachingService;

    private RecipeSaveReq recipeSaveReq;
    private RecipeInfo recipeInfo;
    private RecipeInfoSaveReq recipeInfoSaveReq;
    private User user;

    @BeforeEach
    void beforeEach(){
        this.user= spy(User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build());

        this.recipeInfoSaveReq = RecipeInfoSaveReq.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10L)
                .cookingTime(20L)
                .ingredentCost(30L)
                .build();

        this.recipeSaveReq = RecipeSaveReq.builder()
                .recipeInfoSaveReq(recipeInfoSaveReq)
                .build();

        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testName")
                .recipeWays(new ArrayList<>())
                .ingredients(new ArrayList<>())
                .calogy(1L)
                .build();
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        recipeService.addRecipe(recipeSaveReq,1L);

        //then
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientService,times(1)).addIngredients(any(),any(RecipeInfo.class));
        verify(recipeWayService,times(1)).addRecipeWay(any(),any(RecipeInfo.class));
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try {
            recipeService.addRecipe(recipeSaveReq,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(ingredientService,times(0)).addIngredients(any(),any(RecipeInfo.class));
            verify(recipeWayService,times(0)).addRecipeWay(any(),any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공)")
    public void updateRecipeSuccess(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        RecipeSaveReq recipeSaveReq = RecipeSaveReq.builder()
                .recipeInfoSaveReq(recipeInfoSaveReq)
                .build();
        given(user.getUserId()).willReturn(1L);

        //when
        recipeService.updateRecipe(recipeSaveReq,1L,1L);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientService,times(1)).updateIngredients(any(),any(RecipeInfo.class));
        verify(recipeWayService,times(1)).updateRecipeWayList(any(),any(RecipeInfo.class));
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10L);
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 레시피 수정권한 없음)")
    public void updateRecipeFail1(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .build();
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when, then
        try {
            recipeService.updateRecipe(recipeSaveReq,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(ingredientService,times(0)).updateIngredients(any(),any(RecipeInfo.class));
            verify(recipeWayService,times(0)).updateRecipeWayList(any(),any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_RECIPE);
        }
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void updateRecipeFail2(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when, then
        try {
            recipeService.updateRecipe(recipeSaveReq,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(ingredientService,times(0)).updateIngredients(any(),any(RecipeInfo.class));
            verify(recipeWayService,times(0)).updateRecipeWayList(any(),any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (성공)")
    public void deleteRecipeSuccess(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        given(user.getUserId()).willReturn(1L);

        //when
        recipeService.deleteRecipe(1L,1L);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).delete(recipeInfo);
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteRecipeFail1(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when, then
        try{
            recipeService.deleteRecipe(null,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).delete(recipeInfo);
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 레시피 삭제권한 없음)")
    public void deleteRecipeFail2(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .build();
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when, then
        try{
            recipeService.deleteRecipe(null,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).delete(recipeInfo);
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_RECIPE);
        }
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (성공: 첫번째 페이지인 경우)")
    public void readRecipeSuccess(){
        //given
        RecipeInfoFindRes recipeInfoFindRes = RecipeInfoFindRes
                .toDTO(recipeInfo,1L,"writerNickName");
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        given(recipeCachingService.findRecipeByCache(any(Long.class),any(Boolean.class))).willReturn(RecipeFindRes.builder()
                        .recipeInfoFindRes(RecipeInfoFindRes.builder().build())
                        .recipeWayFindResList(new ArrayList<>())
                        .ingredientFindResList(new ArrayList<>())
                        .build());

        //when
        recipeService.findRecipe(1L,1L, ReplySortType.LIKE,null,null);

        //then
        verify(replyService,times(1))
                .findReplyList(any(Long.class),any(Long.class), any(ReplySortType.class),any(),any(),any(Boolean.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (성공: n번째 페이지인 경우)")
    public void readRecipeSuccess2(){
        //given

        //when
        recipeService.findRecipe(1L,1L, ReplySortType.LIKE,1L,12);

        //then
        verify(replyService,times(1))
                .findReplyList(any(Long.class),any(Long.class), any(ReplySortType.class),any(Long.class),any(),any(Boolean.class));
        verify(recipeInfoRepository,times(0)).findRecipeInfo(any(Long.class));
        verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
    }
}
