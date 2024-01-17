package com.example.foodking.RecipeInfo;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.SaveRecipeReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.Ingredient.IngredientRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import com.example.foodking.Recipe.RecipeService;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
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
    private RecipeService recipeService;
    @Mock
    private UserRepository userRepository;
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
    private User user;

    @BeforeEach
    void beforeEach(){
        this.user= User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();

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

        this.saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
                .saveIngredientReqDTOList(saveIngredientReqDTOList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();

        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .build();

        recipeInfo.changeRecipeWayInfoList(SaveRecipeReqDTO.toRecipeWayInfoListEntity(saveRecipeWayInfoReqDTOList,recipeInfo));
        recipeInfo.changeIngredientList(SaveRecipeReqDTO.toIngredientListEntity(saveIngredientReqDTOList,recipeInfo));
    }

    @Test
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(this.user));

        //when
        recipeService.addRecipe(saveRecipeReqDTO,1l);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(ingredientRepository,times(1)).saveAll(any(List.class));
        verify(recipeWayInfoRepository,times(1)).saveAll(any(List.class));
    }

    /*
        MockMultipartFile은 Spring Framework에서 제공하는 테스트용 MultipartFile 구현체로
        MultipartFile 객체를 흉내내어 생성하는 데 사용된다. 파라미터는 다음과 같다
        name : 클라이언트에서 multipartFile을 업로드할때 지정하는 이름
        originFileName : 기존 파일 명
        contentType : 파일의 컨텐츠 타입 또는 MIME 타입
        content : 업로드된 파일의 바이트 배열, mock객체이기때문에 실제파일의 바이트배열이 아니여도 된다.
    */
    @Test
    @DisplayName("이미지 등록테스트 -> (성공)")
    public void addImageSucess() {
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when
        String savedImagePath = recipeService.addImage(newImage,1l);
        File newFile = new File(savedImagePath);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        assertThat(newFile.exists()).isTrue();
    }

    @Test
    @DisplayName("이미지 수정테스트 -> (성공)")
    public void updateImageSucess() throws IOException {
        //given
        recipeInfo.addRecipeImage("C:/upload/testOldImage");
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        MockMultipartFile oldImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        File oldFile = new File(recipeInfo.getRecipeImage());
        oldImage.transferTo(oldFile);
        assertThat(oldFile.exists()).isTrue();

        //when
        String savedImagePath = recipeService.addImage(newImage,1l);
        File newFile = new File(savedImagePath);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        assertThat(newFile.exists()).isTrue();
        assertThat(oldFile.exists()).isFalse();
    }

    @Test
    @DisplayName("이미지 등록/수정 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void updateImageFail1(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when,then
        try{
            recipeService.addImage(newImage,1l);
            fail("예외가 발생하지 않음");

        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }

    @Test
    @DisplayName("이미지 등록/수정 테스트 -> (실패 : 파일이 없음)")
    public void updateImageFail2(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        MockMultipartFile newImage = null;

        //when,then
        try{
            recipeService.addImage(newImage,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_SAVE_FILE);
        }
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (성공)")
    public void deleteImageSuccess() throws IOException {
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        recipeInfo.addRecipeImage("C:/upload/testImage.png"); // 테스트를 위해 임의의 문자열값을 저장
        File file = new File(recipeInfo.getRecipeImage());
        newImage.transferTo(file);
        assertThat(file.exists()).isTrue();

        //when
        recipeService.deleteImage(1l);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        assertThat(file.exists()).isFalse();
        assertThat(recipeInfo.getRecipeImage()).isEqualTo(null);
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteImageFail1(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            recipeService.deleteImage(1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (실패 : 삭제할 파일 없음)")
    public void deleteImageFail2(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when,then
        try{
            recipeService.deleteImage(1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_FILE);
        }
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (성공)")
    public void updateRecipeSuccess(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when
        recipeService.updateRecipe(saveRecipeReqDTO,1l,1l);

        //then
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (성공)")
    public void deleteRecipeSuccess(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when
        recipeService.deleteRecipe(null,1l);

        //then
        verify(recipeInfoRepository,times(1)).delete(recipeInfo);
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 내가 쓴 레시피가 아님)")
    public void deleteRecipeFail1(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        // when,then
        try {
            recipeService.deleteRecipe(1l, 1l);
            fail("예외전환이 안됨");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_RECIPE);
            verify(recipeInfoRepository,times(0)).delete(recipeInfo);
        }
    }

    @Test
    @DisplayName("DTO와 엔티티간 변환 테스트")
    public void toEntityAndToDtoTest(){
        RecipeInfo recipeInfo = SaveRecipeReqDTO.toRecipeInfoEntity(saveRecipeReqDTO,user);
        assertThat(recipeInfo.getRecipeInfoType()).isEqualTo(RecipeInfoType.KOREAN);
        assertThat(recipeInfo.getRecipeName()).isEqualTo("테스트레시피 이름");
        assertThat(recipeInfo.getRecipeTip()).isEqualTo("테스트레시피 팁");
        assertThat(recipeInfo.getCalogy()).isEqualTo(10l);
        assertThat(recipeInfo.getCookingTime()).isEqualTo(20l);
        assertThat(recipeInfo.getIngredientCost()).isEqualTo(30l);
        System.out.println("AddRecipeReqDTO -> RecipeInfo 변환성공");

        List<Ingredient> ingredientList = SaveRecipeReqDTO.toIngredientListEntity(saveRecipeReqDTO.getSaveIngredientReqDTOList(),recipeInfo);
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

        List<RecipeWayInfo> recipeWayInfoList = SaveRecipeReqDTO.toRecipeWayInfoListEntity(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeInfo);

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
