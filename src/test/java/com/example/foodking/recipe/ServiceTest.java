package com.example.foodking.recipe;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.ingredient.dto.request.SaveIngredientReq;
import com.example.foodking.ingredient.dto.response.ReadIngredientRes;
import com.example.foodking.ingredient.service.IngredientService;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReq;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeRes;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReq;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import com.example.foodking.recipeWayInfo.dto.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipeWayInfo.dto.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.recipeWayInfo.service.RecipeWayInfoService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private RecipeService recipeService;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private RecipeWayInfoService recipeWayInfoService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmotionService emotionService;
    @Mock
    private ReplyService replyService;
    private SaveRecipeReq saveRecipeReq;
    private RecipeInfo recipeInfo;
    private SaveRecipeInfoReq saveRecipeInfoReq;
    private User user;

    @BeforeEach
    void beforeEach(){
        this.user= spy(User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build());

        this.saveRecipeInfoReq = SaveRecipeInfoReq.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10L)
                .cookingTime(20L)
                .ingredentCost(30L)
                .build();

        this.saveRecipeReq = SaveRecipeReq.builder()
                .saveRecipeInfoReq(saveRecipeInfoReq)
                .build();

        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testName")
                .calogy(1L)
                .build();
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        recipeService.addRecipe(saveRecipeReq,1L);

        //then
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try {
            recipeService.addRecipe(saveRecipeReq,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공)")
    public void updateRecipeSuccess(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        SaveRecipeReq saveRecipeReq = SaveRecipeReq.builder()
                .saveRecipeInfoReq(saveRecipeInfoReq)
                .build();
        given(user.getUserId()).willReturn(1L);

        //when
        recipeService.updateRecipe(saveRecipeReq,1L,1L);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
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
            recipeService.updateRecipe(saveRecipeReq,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
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
            recipeService.updateRecipe(saveRecipeReq,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
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
    @DisplayName("레시피 조회 테스트 -> 성공")
    public void readRecipeSuccess(){
        // given
        RecipeWayInfo recipeWayInfo1 = RecipeWayInfo.builder()
                .recipeWay("1")
                .recipeOrder(1L)
                .build();
        RecipeWayInfo recipeWayInfo2 = RecipeWayInfo.builder()
                .recipeWay("2")
                .recipeOrder(2L)
                .build();
        Ingredient ingredient1 = Ingredient.builder()
                .ingredientName("1")
                .ingredientAmount("1")
                .build();
        Ingredient ingredient2 = Ingredient.builder()
                .ingredientName("2")
                .ingredientAmount("2")
                .build();
        RecipeInfo recipeInfo = spy(RecipeInfo.builder()
                .user(user)
                .recipeName("testName")
                .calogy(1L)
                .recipeWayInfoList(List.of(recipeWayInfo1,recipeWayInfo2))
                .ingredientList(List.of(ingredient1,ingredient2))
                .build());
        given(recipeInfo.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,5));
        given(recipeInfo.getModDate()).willReturn(LocalDateTime.of(2024,02,02,06,10));
        given(user.getUserId()).willReturn(2L);
        given(emotionService.readRecipeEmotionCnt(any(RecipeInfo.class))).willReturn(3L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.of(recipeInfo));
        given(replyService.readReply(any(RecipeInfo.class),any(Long.class),any())).willReturn(new ArrayList<>());
        given(recipeInfo.getReplyList()).willReturn(new ArrayList<>());

        // when
        ReadRecipeRes result = recipeService.readRecipe(1L,1L,ReplySortType.LIKE);

        //then
        assertThat(result.getReadRecipeInfoRes().getRegDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,5));
        assertThat(result.getReadRecipeInfoRes().getModDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,10));
        assertThat(result.isMyRecipe()).isFalse();
        assertThat(result.getReadRecipeInfoRes().getEmotionCnt()).isEqualTo(3L);
        assertThat(result.getReadIngredientResList().size()).isEqualTo(2L);
        assertThat(result.getReadIngredientResList().get(0).getIngredientName()).isEqualTo("1");
        assertThat(result.getReadIngredientResList().get(0).getIngredientAmount()).isEqualTo("1");
        assertThat(result.getReadIngredientResList().get(1).getIngredientName()).isEqualTo("2");
        assertThat(result.getReadIngredientResList().get(1).getIngredientAmount()).isEqualTo("2");
        assertThat(result.getReadRecipeWayInfoResDTOList().size()).isEqualTo(2L);
        assertThat(result.getReadRecipeWayInfoResDTOList().get(0).getRecipeWay()).isEqualTo("1");
        assertThat(result.getReadRecipeWayInfoResDTOList().get(0).getRecipeOrder()).isEqualTo(1L);
        assertThat(result.getReadRecipeWayInfoResDTOList().get(1).getRecipeWay()).isEqualTo("2");
        assertThat(result.getReadRecipeWayInfoResDTOList().get(1).getRecipeOrder()).isEqualTo(2L);
        assertThat(result.getReadReplyResList().size()).isEqualTo(0);
        verify(replyService,times(1)).readReply(any(RecipeInfo.class),any(Long.class),any());
        verify(emotionService,times(1)).readRecipeEmotionCnt(any(RecipeInfo.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void readRecipeFail(){
        // given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when,then
        try{
            recipeService.readRecipe(1L,1L, ReplySortType.LATEST);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("DTO -> 엔티티 변환 테스트")
    public void toEntityTest(){
        RecipeInfo recipeInfo = SaveRecipeInfoReq.toEntity(saveRecipeInfoReq,user);
        assertThat(recipeInfo.getRecipeInfoType()).isEqualTo(RecipeInfoType.KOREAN);
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10L);
        assertThat(recipeInfo.getCookingTime()).isEqualTo(20L);
        assertThat(recipeInfo.getIngredientCost()).isEqualTo(30L);
        System.out.println("SaveRecipeReqDTO -> RecipeInfo 변환성공");

        SaveIngredientReq saveIngredientReq = SaveIngredientReq.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료수량1")
                .build();
        Ingredient ingredient = SaveIngredientReq.toEntity(saveIngredientReq,recipeInfo);
        assertThat(ingredient.getIngredientName()).isEqualTo("재료명1");
        assertThat(ingredient.getIngredientAmount()).isEqualTo("재료수량1");
        assertThat(ingredient.getRecipeInfo()).isEqualTo(recipeInfo);
        System.out.println("SaveIngredientReqDTO -> Ingredient 변환성공");

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1L)
                .recipeWay("조리법1")
                .build();
        RecipeWayInfo recipeWayInfo = SaveRecipeWayInfoReqDTO.toEntity(saveRecipeWayInfoReqDTO,recipeInfo);
        assertThat(recipeWayInfo.getRecipeWay()).isEqualTo("조리법1");
        assertThat(recipeWayInfo.getRecipeOrder()).isEqualTo(1L);
        assertThat(ingredient.getRecipeInfo()).isEqualTo(recipeInfo);
        System.out.println("SaveIngredientReqDTO -> Ingredient 변환성공");
    }

    @Test
    @DisplayName("엔티티 -> DTO 변환 테스트")
    public void toDTOTest(){
        ReadRecipeInfoRes readRecipeInfoRes = ReadRecipeInfoRes.toDTO
                (recipeInfo,1L,2L,1L, "writerNickName");
        assertThat(readRecipeInfoRes.getCalogy()).isEqualTo(recipeInfo.getCalogy());
        assertThat(readRecipeInfoRes.getRecipeName()).isEqualTo(recipeInfo.getRecipeName());
        assertThat(readRecipeInfoRes.getEmotionCnt()).isEqualTo(2L);
        assertThat(readRecipeInfoRes.getReplyCnt()).isEqualTo(1L);
        System.out.println("RecipeInfo -> ReadRecipeInfoResDTO 변환성공");

        RecipeWayInfo recipeWayInfo = RecipeWayInfo.builder()
                .recipeWay("recipeWayTest")
                .recipeOrder(1L)
                .build();
        ReadRecipeWayInfoResDTO readRecipeWayInfoResDTO = ReadRecipeWayInfoResDTO.toDTO(recipeWayInfo);
        assertThat(readRecipeWayInfoResDTO.getRecipeWay()).isEqualTo("recipeWayTest");
        assertThat(readRecipeWayInfoResDTO.getRecipeOrder()).isEqualTo(1L);
        System.out.println("RecipeWayInfo -> ReadRecipeWayInfoResDTO 변환성공");

        Ingredient ingredient = Ingredient.builder()
                .ingredientName("testIngredientName")
                .ingredientAmount("testAmount")
                .build();
        ReadIngredientRes readIngredientRes = ReadIngredientRes.toDTO(ingredient);
        assertThat(readIngredientRes.getIngredientName()).isEqualTo("testIngredientName");
        assertThat(readIngredientRes.getIngredientAmount()).isEqualTo("testAmount");
        System.out.println("Ingredient -> ReadIngredientResDTO 변환성공");

    }
}
