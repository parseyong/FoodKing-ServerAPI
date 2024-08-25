package com.example.foodking.recipeImage;

import com.amazonaws.services.s3.AmazonS3;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipeImageService;
import com.example.foodking.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private RecipeImageService recipeImageService;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;
    @Mock
    private AmazonS3 amazonS3;

    private RecipeInfo recipeInfo;
    private User user;

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
                .build();
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
    public void addImageSucess() throws MalformedURLException {
        //given
        given(user.getUserId()).willReturn(1L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        given(amazonS3.putObject(any(),any(),any(),any())).willReturn(null);
        given(amazonS3.getUrl(any(),any())).willReturn(new URL("https://example.com"));

        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when
        recipeImageService.addImage(newImage,1L,1L);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
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
            recipeImageService.addImage(newImage,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }

    @Test
    @DisplayName("이미지 등록/수정 테스트 -> (실패 : 파일이 없음)")
    public void updateImageFail2(){
        //given
        given(user.getUserId()).willReturn(1L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        MockMultipartFile newImage = null;

        //when,then
        try{
            recipeImageService.addImage(newImage,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.INVALID_SAVE_FILE);
        }
    }

    @Test
    @DisplayName("이미지 등록/수정 테스트 -> (실패 : 파일수정 권한 없음)")
    public void updateImageFail3() throws IOException {
        //given
        given(user.getUserId()).willReturn(2L);
        recipeInfo.addRecipeImage("C:/upload/testOldImage");
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        MockMultipartFile oldImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when,then
        try{
            recipeImageService.addImage(newImage,1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_FILE);
        }
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (성공)")
    public void deleteImageSuccess() throws IOException {
        //given
        given(user.getUserId()).willReturn(1L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        recipeInfo.addRecipeImage("https://example.com/test"); // 테스트를 위해 임의의 문자열값을 저장

        //when
        recipeImageService.deleteImage(1L,1L);

        //then
        verify(recipeInfoRepository,times(1)).save(any(RecipeInfo.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        assertThat(recipeInfo.getRecipeImage()).isEqualTo(null);
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteImageFail1(){
        //given
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            recipeImageService.deleteImage(1L,1L);
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
        given(user.getUserId()).willReturn(1L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when,then
        try{
            recipeImageService.deleteImage(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_FILE);
        }
    }

    @Test
    @DisplayName("이미지 삭제 테스트 -> (실패 : 파일 삭제권한 없음)")
    public void deleteImageFail3() throws IOException {
        //given
        given(user.getUserId()).willReturn(2L);
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        recipeInfo.addRecipeImage("C:/upload/testImage.png"); // 테스트를 위해 임의의 문자열값을 저장

        //when,then
        try{
            recipeImageService.deleteImage(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            //then
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).save(any(RecipeInfo.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_FILE);
        }
    }
}
