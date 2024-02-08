package com.example.foodking.recipe;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.domain.Ingredient;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.domain.RecipeWayInfo;
import com.example.foodking.recipe.dto.ingredient.request.SaveIngredientReqDTO;
import com.example.foodking.recipe.dto.ingredient.response.ReadIngredientResDTO;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReqDTO;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeResDTO;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReqDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.recipe.repository.IngredientRepository;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.repository.RecipeWayInfoRepository;
import com.example.foodking.recipe.service.RecipeService;
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
import java.util.stream.Collectors;

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
    private IngredientRepository ingredientRepository;
    @Mock
    private RecipeWayInfoRepository recipeWayInfoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmotionService emotionService;
    @Mock
    private ReplyService replyService;

    private List<SaveIngredientReqDTO> saveIngredientReqDTOList;
    private List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;
    private SaveRecipeReqDTO saveRecipeReqDTO;
    private RecipeInfo recipeInfo;
    private List<RecipeWayInfo> recipeWayInfoList;
    private List<Ingredient> ingredientList;
    private SaveRecipeInfoReqDTO saveRecipeInfoReqDTO;
    private User user;

    @BeforeEach
    void beforeEach(){
        this.user= spy(User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build());
        this.recipeWayInfoList = new ArrayList<>();
        this.ingredientList = new ArrayList<>();

        SaveIngredientReqDTO saveIngredientReqDTO1 = SaveIngredientReqDTO.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료수량1")
                .build();
        SaveIngredientReqDTO saveIngredientReqDTO2 = SaveIngredientReqDTO.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료수량2")
                .build();

        this.saveIngredientReqDTOList = new ArrayList<>(List.of(saveIngredientReqDTO1, saveIngredientReqDTO2));

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO1 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("조리법1")
                .build();
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO2 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(2l)
                .recipeWay("조리법2")
                .build();
        this.saveRecipeWayInfoReqDTOList = new ArrayList<>(List.of(saveRecipeWayInfoReqDTO1, saveRecipeWayInfoReqDTO2));

        this.saveRecipeInfoReqDTO = SaveRecipeInfoReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
                .build();

        this.saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .saveRecipeInfoReqDTO(saveRecipeInfoReqDTO)
                .saveIngredientReqDTOList(saveIngredientReqDTOList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();

        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testName")
                .calogy(1l)
                .recipeWayInfoList(recipeWayInfoList)
                .ingredientList(ingredientList)
                .build();
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        recipeService.addRecipe(saveRecipeReqDTO,1l);

        //then
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientRepository,times(1)).saveAll(any(List.class));
        verify(recipeWayInfoRepository,times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try {
            recipeService.addRecipe(saveRecipeReqDTO,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(ingredientRepository,times(0)).saveAll(any(List.class));
            verify(recipeWayInfoRepository,times(0)).saveAll(any(List.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 재료,조리법이 줄어든 경우)")
    public void updateRecipeSuccess(){
        //given
        List<Ingredient> ingredientList = saveIngredientReqDTOList.stream()
                .map(dto -> SaveIngredientReqDTO.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());

        List<RecipeWayInfo> recipeWayInfoList = saveRecipeWayInfoReqDTOList.stream()
                .map(dto -> SaveRecipeWayInfoReqDTO.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeInfo.changeRecipeWayInfoList(recipeWayInfoList);
        recipeInfo.changeIngredientList(ingredientList);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        SaveRecipeReqDTO saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .saveRecipeInfoReqDTO(saveRecipeInfoReqDTO)
                .saveIngredientReqDTOList(new ArrayList<>())
                .saveRecipeWayInfoReqDTOList(new ArrayList<>())
                .build();
        given(user.getUserId()).willReturn(1l);

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(2l);
        assertThat(recipeInfo.getIngredientList().size()).isEqualTo(2l);
        recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(0);
        assertThat(recipeInfo.getIngredientList().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 재료,조리법이 추가된 경우)")
    public void updateRecipeSuccess2(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        given(user.getUserId()).willReturn(1l);

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().size()).isEqualTo(0l);
        recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeOrder()).isEqualTo(1l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeWay()).isEqualTo("조리법1");
        assertThat(recipeInfo.getRecipeWayInfoList().get(1).getRecipeOrder()).isEqualTo(2l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(1).getRecipeWay()).isEqualTo("조리법2");
        assertThat(recipeInfo.getIngredientList().get(0).getIngredientName()).isEqualTo("재료명1");
        assertThat(recipeInfo.getIngredientList().get(0).getIngredientAmount()).isEqualTo("재료수량1");
        assertThat(recipeInfo.getIngredientList().get(1).getIngredientName()).isEqualTo("재료명2");
        assertThat(recipeInfo.getIngredientList().get(1).getIngredientAmount()).isEqualTo("재료수량2");
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공 : 재료,조리법 갯수 변경이 없는 경우)")
    public void updateRecipeSuccess3(){
        //given
        given(user.getUserId()).willReturn(1l);
        List<Ingredient> ingredientList = saveIngredientReqDTOList.stream()
                .map(dto -> SaveIngredientReqDTO.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());

        List<RecipeWayInfo> recipeWayInfoList = saveRecipeWayInfoReqDTOList.stream()
                .map(dto -> SaveRecipeWayInfoReqDTO.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeInfo.changeRecipeWayInfoList(recipeWayInfoList);
        recipeInfo.changeIngredientList(ingredientList);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        SaveIngredientReqDTO saveIngredientReqDTO1 = SaveIngredientReqDTO.builder()
                .ingredientName("수정된 재료명1")
                .ingredientAmount("수정된 재료수량1")
                .build();
        SaveIngredientReqDTO saveIngredientReqDTO2 = SaveIngredientReqDTO.builder()
                .ingredientName("수정된 재료명2")
                .ingredientAmount("수정된 재료수량2")
                .build();

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO1 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("수정된 조리법1")
                .build();
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO2 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(2l)
                .recipeWay("수정된 조리법2")
                .build();

        SaveRecipeReqDTO saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .saveRecipeInfoReqDTO(saveRecipeInfoReqDTO)
                .saveIngredientReqDTOList(new ArrayList<>(List.of(saveIngredientReqDTO1, saveIngredientReqDTO2)))
                .saveRecipeWayInfoReqDTOList(new ArrayList<>(List.of(saveRecipeWayInfoReqDTO1, saveRecipeWayInfoReqDTO2)))
                .build();

        //when
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeWay()).isEqualTo("조리법1");
        recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeOrder()).isEqualTo(1l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(0).getRecipeWay()).isEqualTo("수정된 조리법1");
        assertThat(recipeInfo.getRecipeWayInfoList().get(1).getRecipeOrder()).isEqualTo(2l);
        assertThat(recipeInfo.getRecipeWayInfoList().get(1).getRecipeWay()).isEqualTo("수정된 조리법2");
        assertThat(recipeInfo.getIngredientList().get(0).getIngredientName()).isEqualTo("수정된 재료명1");
        assertThat(recipeInfo.getIngredientList().get(0).getIngredientAmount()).isEqualTo("수정된 재료수량1");
        assertThat(recipeInfo.getIngredientList().get(1).getIngredientName()).isEqualTo("수정된 재료명2");
        assertThat(recipeInfo.getIngredientList().get(1).getIngredientAmount()).isEqualTo("수정된 재료수량2");
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 레시피 수정권한 없음)")
    public void updateRecipeFail1(){
        //given
        RecipeInfo recipeInfo = RecipeInfo.builder()
                .ingredientList(ingredientList)
                .recipeWayInfoList(recipeWayInfoList)
                .build();
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when, then
        try {
            recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);
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
            recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);
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
        given(user.getUserId()).willReturn(1l);

        //when
        recipeService.deleteRecipe(1l,1l);

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
            recipeService.deleteRecipe(null,1l);
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
                .recipeWayInfoList(recipeWayInfoList)
                .ingredientList(ingredientList)
                .build();
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when, then
        try{
            recipeService.deleteRecipe(null,1l);
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
                .recipeOrder(1l)
                .build();
        RecipeWayInfo recipeWayInfo2 = RecipeWayInfo.builder()
                .recipeWay("2")
                .recipeOrder(2l)
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
                .calogy(1l)
                .recipeWayInfoList(List.of(recipeWayInfo1,recipeWayInfo2))
                .ingredientList(List.of(ingredient1,ingredient2))
                .build());
        given(recipeInfo.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,5));
        given(recipeInfo.getModDate()).willReturn(LocalDateTime.of(2024,02,02,06,10));
        given(user.getUserId()).willReturn(2l);
        given(emotionService.readRecipeEmotionCnt(any(RecipeInfo.class))).willReturn(3l);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.of(recipeInfo));
        given(replyService.readReply(any(RecipeInfo.class),any(Long.class),any())).willReturn(new ArrayList<>());
        given(recipeInfo.getReplyList()).willReturn(new ArrayList<>());

        // when
        ReadRecipeResDTO result = recipeService.readRecipe(1l,1l,ReplySortType.LIKE);

        //then
        assertThat(result.getReadRecipeInfoResDTO().getRegDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,5));
        assertThat(result.getReadRecipeInfoResDTO().getModDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,10));
        assertThat(result.isMyRecipe()).isFalse();
        assertThat(result.getReadRecipeInfoResDTO().getEmotionCnt()).isEqualTo(3l);
        assertThat(result.getReadIngredientResDTOList().size()).isEqualTo(2l);
        assertThat(result.getReadIngredientResDTOList().get(0).getIngredientName()).isEqualTo("1");
        assertThat(result.getReadIngredientResDTOList().get(0).getIngredientAmount()).isEqualTo("1");
        assertThat(result.getReadIngredientResDTOList().get(1).getIngredientName()).isEqualTo("2");
        assertThat(result.getReadIngredientResDTOList().get(1).getIngredientAmount()).isEqualTo("2");
        assertThat(result.getReadRecipeWayInfoResDTOList().size()).isEqualTo(2l);
        assertThat(result.getReadRecipeWayInfoResDTOList().get(0).getRecipeWay()).isEqualTo("1");
        assertThat(result.getReadRecipeWayInfoResDTOList().get(0).getRecipeOrder()).isEqualTo(1l);
        assertThat(result.getReadRecipeWayInfoResDTOList().get(1).getRecipeWay()).isEqualTo("2");
        assertThat(result.getReadRecipeWayInfoResDTOList().get(1).getRecipeOrder()).isEqualTo(2l);
        assertThat(result.getReadReplyResDTOList().size()).isEqualTo(0);
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
            recipeService.readRecipe(1l,1l, ReplySortType.LATEST);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("DTO -> 엔티티 변환 테스트")
    public void toEntityTest(){
        RecipeInfo recipeInfo = SaveRecipeInfoReqDTO.toEntity(saveRecipeInfoReqDTO,user);
        assertThat(recipeInfo.getRecipeInfoType()).isEqualTo(RecipeInfoType.KOREAN);
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getCookingTime()).isEqualTo(20l);
        assertThat(recipeInfo.getIngredientCost()).isEqualTo(30l);
        System.out.println("SaveRecipeReqDTO -> RecipeInfo 변환성공");

        SaveIngredientReqDTO saveIngredientReqDTO = SaveIngredientReqDTO.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료수량1")
                .build();
        Ingredient ingredient = SaveIngredientReqDTO.toEntity(saveIngredientReqDTO,recipeInfo);
        assertThat(ingredient.getIngredientName()).isEqualTo("재료명1");
        assertThat(ingredient.getIngredientAmount()).isEqualTo("재료수량1");
        assertThat(ingredient.getRecipeInfo()).isEqualTo(recipeInfo);
        System.out.println("SaveIngredientReqDTO -> Ingredient 변환성공");

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("조리법1")
                .build();
        RecipeWayInfo recipeWayInfo = SaveRecipeWayInfoReqDTO.toEntity(saveRecipeWayInfoReqDTO,recipeInfo);
        assertThat(recipeWayInfo.getRecipeWay()).isEqualTo("조리법1");
        assertThat(recipeWayInfo.getRecipeOrder()).isEqualTo(1l);
        assertThat(ingredient.getRecipeInfo()).isEqualTo(recipeInfo);
        System.out.println("SaveIngredientReqDTO -> Ingredient 변환성공");
    }

    @Test
    @DisplayName("엔티티 -> DTO 변환 테스트")
    public void toDTOTest(){
        ReadRecipeInfoResDTO readRecipeInfoResDTO = ReadRecipeInfoResDTO.toDTO(recipeInfo,1l,2l);
        assertThat(readRecipeInfoResDTO.getCalogy()).isEqualTo(recipeInfo.getCalogy());
        assertThat(readRecipeInfoResDTO.getRecipeName()).isEqualTo(recipeInfo.getRecipeName());
        assertThat(readRecipeInfoResDTO.getEmotionCnt()).isEqualTo(2l);
        assertThat(readRecipeInfoResDTO.getReplyCnt()).isEqualTo(1l);
        System.out.println("RecipeInfo -> ReadRecipeInfoResDTO 변환성공");

        RecipeWayInfo recipeWayInfo = RecipeWayInfo.builder()
                .recipeWay("recipeWayTest")
                .recipeOrder(1l)
                .build();
        ReadRecipeWayInfoResDTO readRecipeWayInfoResDTO = ReadRecipeWayInfoResDTO.toDTO(recipeWayInfo);
        assertThat(readRecipeWayInfoResDTO.getRecipeWay()).isEqualTo("recipeWayTest");
        assertThat(readRecipeWayInfoResDTO.getRecipeOrder()).isEqualTo(1l);
        System.out.println("RecipeWayInfo -> ReadRecipeWayInfoResDTO 변환성공");

        Ingredient ingredient = Ingredient.builder()
                .ingredientName("testIngredientName")
                .ingredientAmount("testAmount")
                .build();
        ReadIngredientResDTO readIngredientResDTO = ReadIngredientResDTO.toDTO(ingredient);
        assertThat(readIngredientResDTO.getIngredientName()).isEqualTo("testIngredientName");
        assertThat(readIngredientResDTO.getIngredientAmount()).isEqualTo("testAmount");
        System.out.println("Ingredient -> ReadIngredientResDTO 변환성공");

    }
}
