package com.example.foodking.RecipeInfo;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Ingredient.DTO.AddIngredientReqDTO;
import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.Ingredient.IngredientRepository;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import com.example.foodking.RecipeWayInfo.DTO.AddRecipeWayInfoReqDTO;
import com.example.foodking.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private RecipeInfoService recipeInfoService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private RecipeWayInfoRepository recipeWayInfoRepository;

    private List<AddIngredientReqDTO> addIngredientReqDTOList;
    private List<AddRecipeWayInfoReqDTO> addRecipeWayInfoReqDTOList;
    private AddRecipeReqDTO addRecipeReqDTO;
    private User user;

    @BeforeEach
    void beforeEach(){
        this.user= User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();

        AddIngredientReqDTO addIngredientReqDTO1 = AddIngredientReqDTO.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료수량1")
                .build();
        AddIngredientReqDTO addIngredientReqDTO2 = AddIngredientReqDTO.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료수량2")
                .build();

        this.addIngredientReqDTOList = new ArrayList<>(List.of(addIngredientReqDTO1, addIngredientReqDTO2));

        AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO1 = AddRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("조리법1")
                .build();
        AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO2 = AddRecipeWayInfoReqDTO.builder()
                .recipeOrder(2l)
                .recipeWay("조리법2")
                .build();
        this.addRecipeWayInfoReqDTOList = new ArrayList<>(List.of(addRecipeWayInfoReqDTO1,addRecipeWayInfoReqDTO2));

        this.addRecipeReqDTO = AddRecipeReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
                .addIngredientReqDTOList(addIngredientReqDTOList)
                .addRecipeWayInfoReqDTOList(addRecipeWayInfoReqDTOList)
                .build();
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        recipeInfoService.addRecipeInfo(addRecipeReqDTO,1l);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientRepository,times(1)).saveAll(any(List.class));
        verify(recipeWayInfoRepository,times(1)).saveAll(any(List.class));
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when, then
        try{
            recipeInfoService.addRecipeInfo(addRecipeReqDTO,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(ingredientRepository,times(0)).saveAll(any(List.class));
            verify(recipeWayInfoRepository,times(0)).saveAll(any(List.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("DTO와 엔티티간 변환 테스트")
    public void toEntityAndToDtoTest(){
        RecipeInfo recipeInfo = AddRecipeReqDTO.toRecipeInfoEntity(addRecipeReqDTO,user);
        assertThat(recipeInfo.getRecipeInfoType()).isEqualTo(RecipeInfoType.KOREAN);
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getCookingTime()).isEqualTo(20l);
        assertThat(recipeInfo.getIngredientCost()).isEqualTo(30l);
        System.out.println("AddRecipeReqDTO -> RecipeInfo 변환성공");

        List<Ingredient> ingredientList = AddRecipeReqDTO.toIngredientListEntity(addRecipeReqDTO.getAddIngredientReqDTOList(),recipeInfo);
        if(ingredientList.size() != 2){
            fail("AddRecipeReqDTO -> IngredientList 변환실패");
        }
        assertThat(ingredientList.get(0).getIngredientName()).isEqualTo("재료명1");
        assertThat(ingredientList.get(1).getIngredientName()).isEqualTo("재료명2");

        assertThat(ingredientList.get(0).getRecipeInfo()).isEqualTo(recipeInfo);
        assertThat(ingredientList.get(1).getRecipeInfo()).isEqualTo(recipeInfo);

        assertThat(ingredientList.get(0).getIngredientAmount()).isEqualTo("재료수량1");
        assertThat(ingredientList.get(1).getIngredientAmount()).isEqualTo("재료수량2");
        System.out.println("AddRecipeReqDTO -> IngredientList 변환성공");

        List<RecipeWayInfo> recipeWayInfoList = AddRecipeReqDTO.toRecipeWayInfoListEntity(addRecipeReqDTO.getAddRecipeWayInfoReqDTOList(),recipeInfo);

        if(recipeWayInfoList.size() != 2){
            fail("AddRecipeReqDTO -> recipeWayInfoList 변환실패");
        }

        assertThat(recipeWayInfoList.get(0).getRecipeWay()).isEqualTo("조리법1");
        assertThat(recipeWayInfoList.get(1).getRecipeWay()).isEqualTo("조리법2");

        assertThat(recipeWayInfoList.get(0).getRecipeInfo()).isEqualTo(recipeInfo);
        assertThat(recipeWayInfoList.get(1).getRecipeInfo()).isEqualTo(recipeInfo);

        assertThat(recipeWayInfoList.get(0).getRecipeOrder()).isEqualTo(1l);
        assertThat(recipeWayInfoList.get(1).getRecipeOrder()).isEqualTo(2l);
        System.out.println("AddRecipeReqDTO -> recipeWayInfoList 변환성공");

    }
}
