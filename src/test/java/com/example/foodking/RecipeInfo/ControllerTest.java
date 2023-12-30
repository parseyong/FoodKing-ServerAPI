package com.example.foodking.RecipeInfo;

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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RecipeInfoController.class)
@Import(SecurityConfig.class)
public class ControllerTest {

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private RecipeInfoService recipeInfoService;
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
    @WithMockUser
    @DisplayName("레시피 등록테스트 -> (성공)")
    public void addRecipeInfoSuccess() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);
        given(jwtProvider.readUserIdByToken(any())).willReturn(1l);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("레시피 등록완료"))
                .andDo(print());

        verify(jwtProvider,times(1)).readUserIdByToken(any(HttpServletRequest.class));
        verify(recipeInfoService,times(1)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("레시피 등록테스트 -> (실패 : 인증실패)")
    public void addRecipeInfoFail() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);
        given(jwtProvider.readUserIdByToken(any())).willReturn(1l);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(jwtProvider,times(0)).readUserIdByToken(any(HttpServletRequest.class));
        verify(recipeInfoService,times(0)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 입력값 공백)")
    public void addRecipeInfoFai2() throws Exception {
        //given
        AddRecipeReqDTO addRecipeReqDTO = AddRecipeReqDTO.builder()
                .addIngredientReqDTOList(addIngredientReqDTOList)
                .addRecipeWayInfoReqDTOList(addRecipeWayInfoReqDTOList)
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);
        given(jwtProvider.readUserIdByToken(any())).willReturn(1l);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());

        verify(jwtProvider,times(0)).readUserIdByToken(any(HttpServletRequest.class));
        verify(recipeInfoService,times(0)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }

    @Test
    @WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFai3() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(addRecipeReqDTO);
        given(jwtProvider.readUserIdByToken(any())).willReturn(1l);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(recipeInfoService).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(jwtProvider,times(1)).readUserIdByToken(any(HttpServletRequest.class));
        verify(recipeInfoService,times(1)).addRecipeInfo(any(AddRecipeReqDTO.class),any(Long.class));
    }
}
