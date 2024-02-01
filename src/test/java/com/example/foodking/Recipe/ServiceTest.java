package com.example.foodking.Recipe;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.Request.SaveRecipeReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.Request.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.Ingredient.IngredientRepository;
import com.example.foodking.Recipe.RecipeInfo.DTO.Request.SaveRecipeInfoReqDTO;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.Request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        this.user= User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();
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
                .recipeWayInfoList(recipeWayInfoList)
                .ingredientList(ingredientList)
                .build();
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given

        //when
        recipeService.addRecipe(saveRecipeReqDTO,user);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientRepository,times(1)).saveAll(any(List.class));
        verify(recipeWayInfoRepository,times(1)).saveAll(any(List.class));
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

        //when
        recipeService.deleteRecipe(null,1l);

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
    @DisplayName("DTO와 엔티티간 변환 테스트")
    public void toEntityAndToDtoTest(){
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
}
