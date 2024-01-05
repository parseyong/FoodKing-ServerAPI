package com.example.foodking.RecipeInfo;

import com.example.foodking.Auth.CustomUserDetailsService;
import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Config.SecurityConfig;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Ingredient.DTO.AddIngredientReqDTO;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import com.example.foodking.RecipeWayInfo.DTO.AddRecipeWayInfoReqDTO;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RecipeInfoController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private RecipeInfoService recipeInfoService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    private List<AddIngredientReqDTO> addIngredientReqDTOList;
    private List<AddRecipeWayInfoReqDTO> addRecipeWayInfoReqDTOList;
    private AddRecipeReqDTO addRecipeReqDTO;

    @BeforeEach
    void beforeEach(){
        AddIngredientReqDTO addIngredientReqDTO1 = AddIngredientReqDTO.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료 수량1")
                .build();
        AddIngredientReqDTO addIngredientReqDTO2 = AddIngredientReqDTO.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료 수량2")
                .build();

        this.addIngredientReqDTOList = new ArrayList<>(List.of(addIngredientReqDTO1, addIngredientReqDTO2));

        AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO1 = AddRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("조리법 1")
                .build();
        AddRecipeWayInfoReqDTO addRecipeWayInfoReqDTO2 = AddRecipeWayInfoReqDTO.builder()
                .recipeOrder(2l)
                .recipeWay("조리법 2")
                .build();
        this.addRecipeWayInfoReqDTOList = new ArrayList<>(List.of(addRecipeWayInfoReqDTO1,addRecipeWayInfoReqDTO2));

        this.addRecipeReqDTO = AddRecipeReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트 레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
                .addIngredientReqDTOList(addIngredientReqDTOList)
                .addRecipeWayInfoReqDTOList(addRecipeWayInfoReqDTOList)
                .build();
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("레시피 등록완료"))
                .andDo(print());

        verify(recipeInfoService,times(1)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("레시피 등록테스트 -> (실패 : 인증실패)")
    public void addRecipeInfoFail() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeInfoService,times(0)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 입력값 공백)")
    public void addRecipeInfoFai2() throws Exception {
        //given
        makeAuthentication();
        AddRecipeReqDTO addRecipeReqDTO = AddRecipeReqDTO.builder()
                .addIngredientReqDTOList(addIngredientReqDTOList)
                .addRecipeWayInfoReqDTOList(addRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(recipeInfoService,times(0)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFai3() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(recipeInfoService).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(recipeInfoService,times(1)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (성공)")
    public void addImageSuccess() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("이미지 등록완료"));

        verify(recipeInfoService,times(1)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : 인증실패)")
    public void addImageFail1() throws Exception {
        //given
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"));

        verify(recipeInfoService,times(0)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : pathVariable 타입예외)")
    public void addImageFail2() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", "ㅎㅇ")
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andExpect(jsonPath("$.data.fieldName").value("recipeInfoId"))
                .andExpect(jsonPath("$.data.requiredType").value("Long"));

        verify(recipeInfoService,times(0)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : pathVariable값 공백)")
    public void addImageFail3() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", " ")
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."));

        verify(recipeInfoService,times(0)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : IOException)")
    public void addImageFail4() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        given(recipeInfoService.addImage(any(MultipartFile.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.FILE_IOEXCEPTION));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("파일 저장중 문제가 발생했습니다."))
                .andExpect(jsonPath("$.data.recipeImage").value("파일 저장중 문제가 발생했습니다."));

        verify(recipeInfoService,times(1)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : 파일이 없거나 저장할 수 없는파일)")
    public void addImageFail5() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        given(recipeInfoService.addImage(any(MultipartFile.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.INVALID_SAVE_FILE));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("등록할 파일이 존재하지 않습니다. 파일을 추가해주세요."))
                .andExpect(jsonPath("$.data.recipeImage").value("등록할 파일이 존재하지 않습니다. 파일을 추가해주세요."));

        verify(recipeInfoService,times(1)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 이미지 등록테스트 -> (실패 : 존재하지 않는 레시피)")
    public void addImageFail6() throws Exception {
        //given
        makeAuthentication();
        MockMultipartFile newImage = new MockMultipartFile(
                "recipeImage", "testImage.png", "image/png", "test image content".getBytes()
        );
        given(recipeInfoService.addImage(any(MultipartFile.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andExpect(jsonPath("$.data.recipeInfoId").value("존재하지 않는 레시피입니다"));

        verify(recipeInfoService,times(1)).addImage(any(MultipartFile.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (성공)")
    public void deleteImageSuccess() throws Exception {
        //when, then
        makeAuthentication();
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이미지 삭제완료"))
                .andDo(print());
        verify(recipeInfoService,times(1)).deleteImage(any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : 인증되지 않은 유저)")
    public void deleteImageFail1() throws Exception {
        //when, then
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());
        verify(recipeInfoService,times(0)).deleteImage(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteImageFail2() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(recipeInfoService).deleteImage(any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());
        verify(recipeInfoService,times(1)).deleteImage(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : 삭제할 파일이 없음)")
    public void deleteImageFail3() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_FILE)).when(recipeInfoService).deleteImage(any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("파일이 존재하지 않습니다"))
                .andDo(print());
        verify(recipeInfoService,times(1)).deleteImage(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : pathVariable 타입예외)")
    public void deleteImageFail4() throws Exception {
        //when, then
        makeAuthentication();
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andExpect(jsonPath("$.data.fieldName").value("recipeInfoId"))
                .andExpect(jsonPath("$.data.requiredType").value("Long"))
                .andDo(print());

        verify(recipeInfoService,times(0)).deleteImage(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : pathVariable 공백)")
    public void deleteImageFail5() throws Exception {
        //when, then
        makeAuthentication();
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}"," ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeInfoService,times(0)).deleteImage(any(Long.class));
    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1l,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
