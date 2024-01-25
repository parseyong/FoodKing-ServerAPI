package com.example.foodking.RecipeInfo;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Config.SecurityConfig;
import com.example.foodking.Recipe.RecipeController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@WebMvcTest(value = RecipeController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {
    /*
    @MockBean
    private RecipeService recipeService;
    @MockBean
    private UserService userService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    private List<SaveIngredientReqDTO> saveIngredientReqDTOList;
    private List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;
    private SaveRecipeReqDTO saveRecipeReqDTO;
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
                .ingredientAmount("재료 수량1")
                .build();
        SaveIngredientReqDTO saveIngredientReqDTO2 = SaveIngredientReqDTO.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료 수량2")
                .build();

        this.saveIngredientReqDTOList = new ArrayList<>(List.of(saveIngredientReqDTO1, saveIngredientReqDTO2));

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO1 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1l)
                .recipeWay("조리법 1")
                .build();
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO2 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(2l)
                .recipeWay("조리법 2")
                .build();
        this.saveRecipeWayInfoReqDTOList = new ArrayList<>(List.of(saveRecipeWayInfoReqDTO1, saveRecipeWayInfoReqDTO2));

        this.saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트 레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
                .saveIngredientReqDTOList(saveIngredientReqDTOList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);
        given(userService.findUserById(any(Long.class))).willReturn(this.user);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("레시피 등록완료"))
                .andDo(print());

        verify(recipeService,times(1)).addRecipe(any(SaveRecipeReqDTO.class),any(User.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("레시피 등록테스트 -> (실패 : 인증실패)")
    public void addRecipeInfoFail() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);
        given(userService.findUserById(any(Long.class))).willReturn(this.user);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReqDTO.class),any(User.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 입력값 공백)")
    public void addRecipeInfoFai2() throws Exception {
        //given
        makeAuthentication();
        SaveRecipeReqDTO saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .saveIngredientReqDTOList(saveIngredientReqDTOList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReqDTO.class),any(User.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFai3() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(userService).findUserById(any(Long.class));

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReqDTO.class),any(User.class));
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

        verify(recipeService,times(1)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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

        verify(recipeService,times(0)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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

        verify(recipeService,times(0)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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

        verify(recipeService,times(0)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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
        given(recipeService.addImage(any(MultipartFile.class),any(Long.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.FILE_IOEXCEPTION));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("파일 저장중 문제가 발생했습니다."))
                .andExpect(jsonPath("$.data.recipeImage").value("파일 저장중 문제가 발생했습니다."));

        verify(recipeService,times(1)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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
        given(recipeService.addImage(any(MultipartFile.class),any(Long.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.INVALID_SAVE_FILE));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("등록할 파일이 존재하지 않습니다. 파일을 추가해주세요."))
                .andExpect(jsonPath("$.data.recipeImage").value("등록할 파일이 존재하지 않습니다. 파일을 추가해주세요."));

        verify(recipeService,times(1)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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
        given(recipeService.addImage(any(MultipartFile.class),any(Long.class),any(Long.class))).willThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        //when, then
        this.mockMvc.perform(multipart("/recipes/images/{recipeInfoId}", 1l)
                        .file(newImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(recipeService,times(1)).addImage(any(MultipartFile.class),any(Long.class),any(Long.class));
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
        verify(recipeService,times(1)).deleteImage(any(Long.class),any(Long.class));
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
        verify(recipeService,times(0)).deleteImage(any(Long.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteImageFail2() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(recipeService).deleteImage(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());
        verify(recipeService,times(1)).deleteImage(any(Long.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("이미지 삭제 테스트 -> (실패 : 삭제할 파일이 없음)")
    public void deleteImageFail3() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_FILE)).when(recipeService).deleteImage(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/images/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("파일이 존재하지 않습니다"))
                .andDo(print());
        verify(recipeService,times(1)).deleteImage(any(Long.class),any(Long.class));
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

        verify(recipeService,times(0)).deleteImage(any(Long.class),any(Long.class));
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

        verify(recipeService,times(0)).deleteImage(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> 성공")
    public void updateRecipeSuccess() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 수정완료"))
                .andDo(print());

        verify(recipeService,times(1)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 인증되지 않은 유저)")
    public void updateRecipeFail1() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 입력값 공백)")
    public void updateRecipeFail2() throws Exception {
        //given
        SaveRecipeReqDTO saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("")
                .recipeTip("")
                .calogy(null)
                .cookingTime(null)
                .ingredentCost(null)
                .saveIngredientReqDTOList(saveIngredientReqDTOList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(recipeService,times(0)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : Pathvariable값 타입 예외)")
    public void updateRecipeFail3() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andExpect(jsonPath("$.data.fieldName").value("recipeInfoId"))
                .andExpect(jsonPath("$.data.requiredType").value("Long"))
                .andDo(print());

        verify(recipeService,times(0)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : Pathvariable값 공백)")
    public void updateRecipeFail4() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeService,times(0)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void updateRecipeFail6() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(recipeService).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(recipeService,times(1)).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> 성공")
    public void deleteRecipeSuccess() throws Exception {
        //given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 삭제완료"))
                .andDo(print());

        verify(recipeService,times(1)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 인증되지 않은 유저)")
    public void deleteRecipeFail1() throws Exception {
        //given

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : Pathvariable값 타입 예외)")
    public void deleteRecipeFail2() throws Exception {
        //given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andExpect(jsonPath("$.data.fieldName").value("recipeInfoId"))
                .andExpect(jsonPath("$.data.requiredType").value("Long"))
                .andDo(print());

        verify(recipeService,times(0)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : Pathvariable값 공백)")
    public void deleteRecipeFail3() throws Exception {
        //given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}"," ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeService,times(0)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteRecipeFail5() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO)).when(recipeService).deleteRecipe(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(recipeService,times(1)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 레시피 삭제권한 없음)")
    public void deleteRecipeFail6() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_RECIPE)).when(recipeService).deleteRecipe(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 레시피에 대해 권한이 없습니다"))
                .andDo(print());

        verify(recipeService,times(1)).deleteRecipe(any(Long.class),any(Long.class));
    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1l,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

     */
}
