package com.example.foodking.recipe;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.dto.request.SaveIngredientReq;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.controller.RecipeController;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReq;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeRes;
import com.example.foodking.recipe.dto.recipeInfo.request.ReadRecipeInfoPagingReq;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReq;
import com.example.foodking.recipe.service.RecipePagingService;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.recipeWayInfo.dto.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.reply.common.ReplySortType;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodking.exception.ExceptionCode.NOT_EXIST_RECIPEINFO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RecipeController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private RecipeService recipeService;
    @MockBean
    private RecipePagingService recipePagingService;
    @MockBean
    private JwtProvider jwtProvider;
    @Autowired
    private MockMvc mockMvc;
    private List<SaveIngredientReq> saveIngredientReqList;
    private List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;
    private SaveRecipeReq saveRecipeReq;
    private SaveRecipeInfoReq saveRecipeInfoReq;

    @BeforeEach
    void beforeEach(){
        this.saveRecipeInfoReq = SaveRecipeInfoReq.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10L)
                .cookingTime(20L)
                .ingredentCost(30L)
                .build();

        SaveIngredientReq saveIngredientReq1 = SaveIngredientReq.builder()
                .ingredientName("재료명1")
                .ingredientAmount("재료 수량1")
                .build();
        SaveIngredientReq saveIngredientReq2 = SaveIngredientReq.builder()
                .ingredientName("재료명2")
                .ingredientAmount("재료 수량2")
                .build();

        this.saveIngredientReqList = new ArrayList<>(List.of(saveIngredientReq1, saveIngredientReq2));

        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO1 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(1L)
                .recipeWay("조리법 1")
                .build();
        SaveRecipeWayInfoReqDTO saveRecipeWayInfoReqDTO2 = SaveRecipeWayInfoReqDTO.builder()
                .recipeOrder(2L)
                .recipeWay("조리법 2")
                .build();
        this.saveRecipeWayInfoReqDTOList = new ArrayList<>(List.of(saveRecipeWayInfoReqDTO1, saveRecipeWayInfoReqDTO2));

        this.saveRecipeReq = SaveRecipeReq.builder()
                .saveRecipeInfoReq(saveRecipeInfoReq)
                .saveIngredientReqList(saveIngredientReqList)
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
        String requestBody = gson.toJson(saveRecipeReq);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("레시피 등록완료"))
                .andDo(print());

        verify(recipeService,times(1)).addRecipe(any(SaveRecipeReq.class),any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("레시피 등록테스트 -> (실패 : 인증실패)")
    public void addRecipeInfoFail() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReq);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReq.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 입력값 공백)")
    public void addRecipeInfoFai2() throws Exception {
        //given
        makeAuthentication();
        SaveRecipeReq saveRecipeReq = SaveRecipeReq.builder()
                .saveIngredientReqList(saveIngredientReqList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReq);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReq.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFai3() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReq);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER))
                .when(recipeService).addRecipe(any(SaveRecipeReq.class),any(Long.class));

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(recipeService,times(1)).addRecipe(any(SaveRecipeReq.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> 성공")
    public void updateRecipeSuccess() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 수정완료"))
                .andDo(print());

        verify(recipeService,times(1))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 인증되지 않은 유저)")
    public void updateRecipeFail1() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 입력값 공백)")
    public void updateRecipeFail2() throws Exception {
        //given
        SaveRecipeInfoReq saveRecipeInfoReq = SaveRecipeInfoReq.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("")
                .recipeTip("")
                .calogy(null)
                .cookingTime(null)
                .ingredentCost(null)
                .build();

        SaveRecipeReq saveRecipeReq = SaveRecipeReq.builder()
                .saveRecipeInfoReq(saveRecipeInfoReq)
                .saveIngredientReqList(saveIngredientReqList)
                .saveRecipeWayInfoReqDTOList(saveRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(recipeService,times(0))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : Pathvariable값 타입 예외)")
    public void updateRecipeFail3() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andDo(print());

        verify(recipeService,times(0))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : Pathvariable값 공백)")
    public void updateRecipeFail4() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);
        makeAuthentication();

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeService,times(0))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void updateRecipeFail6() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);
        makeAuthentication();
        doThrow(new CommondException(NOT_EXIST_RECIPEINFO))
                .when(recipeService).updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(recipeService,times(1))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 수정 테스트 -> (실패 : 레시피 수정권한 없음)")
    public void updateRecipeFail7() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReq);
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_RECIPE))
                .when(recipeService).updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 레시피에 대해 권한이 없습니다"))
                .andDo(print());

        verify(recipeService,times(1))
                .updateRecipe(any(SaveRecipeReq.class),any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> 성공")
    public void deleteRecipeSuccess() throws Exception {
        //given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1L)
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
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1L)
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeService,times(0)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 삭제 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void deleteRecipeFail5() throws Exception {
        //given
        makeAuthentication();
        doThrow(new CommondException(NOT_EXIST_RECIPEINFO))
                .when(recipeService).deleteRecipe(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1L)
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
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_RECIPE))
                .when(recipeService).deleteRecipe(any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(delete("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 레시피에 대해 권한이 없습니다"))
                .andDo(print());

        verify(recipeService,times(1)).deleteRecipe(any(Long.class),any(Long.class));
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> 성공")
    public void readRecipeSuccess() throws Exception {
        // given
        makeAuthentication();
        ReadRecipeRes readRecipeRes = ReadRecipeRes.builder().build();
        given(recipeService.readRecipe(any(Long.class),any(Long.class),any(),any(),any())).willReturn(readRecipeRes);

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE))
                        .param("lastId", "1")
                        .param("lastValue","12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 상세정보 조회완료"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(print());

        verify(recipeService,times(1)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 인증되지 않은 유저)")
    public void readRecipeFail1() throws Exception {
        // given

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : PathVariable 타입예외)")
    public void readRecipeFail2() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}","ㅎㅇ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeInfoId이 Long타입이여야 합니다."))
                .andDo(print());

        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : PathVariable 공백)")
    public void readRecipeFail3() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}"," ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 요청이 아닙니다."))
                .andDo(print());

        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 입력값enum타입 예외)")
    public void readRecipeFail4() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", "df "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("sort이 ReplySortType타입이여야 합니다."))
                .andDo(print());

        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 입력값없음)")
    public void readRecipeFail5() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.sort")
                        .value("Required request parameter 'sort' for method parameter type ReplySortType is not present(관리자에게 문의하세요)"))
                .andDo(print());
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void readRecipeFail6() throws Exception {
        // given
        makeAuthentication();
        given(recipeService.readRecipe(any(Long.class),any(Long.class),any(),any(),any()))
                .willThrow(new CommondException(NOT_EXIST_RECIPEINFO));

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE))
                        .param("lastId", "1")
                        .param("lastValue","12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());

        verify(recipeService,times(1)).readRecipe(any(Long.class),any(Long.class),any(),any(),any());
    }

    @Test
    @DisplayName("레시피타입조회 페이징 테스트 -> 성공")
    public void readRecipeInfoPagingByTypeSuccess() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeType}/list",RecipeInfoType.KOREAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 타입 조회성공"))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("레시피타입조회 페이징 테스트 -> (실패 : RecipeInfoType 타입 예외)")
    public void readRecipeInfoPagingByTypeFail1() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeType}/list","문자")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeType이 RecipeInfoType타입이여야 합니다."))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("레시피타입조회 페이징 테스트 -> (실패 : RecipeSortType 타입 예외)")
    public void readRecipeInfoPagingByTypeFail2() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeType}//list",RecipeInfoType.KOREAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", "문자"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeSortType이 RecipeSortType타입이여야 합니다."))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("레시피타입조회 페이징 테스트 -> (실패 : 인증되지 않음)")
    public void readRecipeInfoPagingByTypeFail3() throws Exception {
        // given

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeType}//list",RecipeInfoType.KOREAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("레시피타입조회 페이징 테스트 -> (실패 : 존재하지 않는 페이지)")
    public void readRecipeInfoPagingByTypeFail4() throws Exception {
        // given
        makeAuthentication();
        given(recipePagingService.readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class)))
                .willThrow(new CommondException(ExceptionCode.NOT_EXIST_PAGE));

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeType}//list",RecipeInfoType.KOREAN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("내가 쓴 레시피 페이징조회 -> 성공")
    public void readMyRecipeInfoPagingSuccess() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/mine/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("내가 쓴 레시피 조회성공"))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("내가 쓴 레시피 페이징조회 -> (실패 : RecipeSortType 타입예외)")
    public void readMyRecipeInfoPagingFail1() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/mine/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", "문자"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeSortType이 RecipeSortType타입이여야 합니다."))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("내가 쓴 레시피 페이징조회 -> (실패 : 인증실패)")
    public void readMyRecipeInfoPagingFail2() throws Exception {
        // given

        //when, then
        this.mockMvc.perform(get("/recipes/mine/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("내가 쓴 레시피 페이징조회 -> (실패 : 존재하지않는 페이지)")
    public void readMyRecipeInfoPagingFail3() throws Exception {
        // given
        makeAuthentication();
        given(recipePagingService.readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class)))
                .willThrow(new CommondException(ExceptionCode.NOT_EXIST_PAGE));

        //when, then
        this.mockMvc.perform(get("/recipes/mine/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readRecipeInfoPagingByCondition(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징조회 -> 성공")
    public void readLikeRecipeInfoPagingSuccess() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/like/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요 누른 레시피 조회성공"))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readLikedRecipeInfoPaging(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징조회 -> (실패 : RecipeSortType 타입예외)")
    public void readLikeRecipeInfoPagingFail1() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/like/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", "문자"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("recipeSortType이 RecipeSortType타입이여야 합니다."))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readLikedRecipeInfoPaging(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징조회 -> (실패 : 인증실패)")
    public void readLikeRecipeInfoPagingFail2() throws Exception {
        // given

        //when, then
        this.mockMvc.perform(get("/recipes/like/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipePagingService,times(0))
                .readLikedRecipeInfoPaging(any(ReadRecipeInfoPagingReq.class));
    }

    @Test
    @DisplayName("좋아요 누른 레시피 페이징조회 -> (실패 : 존재하지 않는 페이지)")
    public void readLikeRecipeInfoPagingFail3() throws Exception {
        // given
        makeAuthentication();
        given(recipePagingService.readLikedRecipeInfoPaging(any(ReadRecipeInfoPagingReq.class)))
                .willThrow(new CommondException(ExceptionCode.NOT_EXIST_PAGE));

        //when, then
        this.mockMvc.perform(get("/recipes/like/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("recipeSortType", String.valueOf(RecipeSortType.LATEST)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 페이지입니다."))
                .andDo(print());

        verify(recipePagingService,times(1))
                .readLikedRecipeInfoPaging(any(ReadRecipeInfoPagingReq.class));
    }
    
    private void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
