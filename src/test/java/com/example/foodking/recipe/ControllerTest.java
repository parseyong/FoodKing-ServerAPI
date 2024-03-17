package com.example.foodking.recipe;

import com.example.foodking.auth.CustomUserDetailsService;
import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.controller.RecipeController;
import com.example.foodking.recipe.dto.ingredient.request.SaveIngredientReqDTO;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReqDTO;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeResDTO;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReqDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipe.service.PagingService;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.user.domain.User;
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
    private PagingService pagingService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private MockMvc mockMvc;
    private List<SaveIngredientReqDTO> saveIngredientReqDTOList;
    private List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList;
    private SaveRecipeReqDTO saveRecipeReqDTO;
    private User user;
    private SaveRecipeInfoReqDTO saveRecipeInfoReqDTO;

    @BeforeEach
    void beforeEach(){
        this.user= User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();

        this.saveRecipeInfoReqDTO = SaveRecipeInfoReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("테스트레시피 이름")
                .recipeTip("테스트레시피 팁")
                .calogy(10l)
                .cookingTime(20l)
                .ingredentCost(30l)
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
                .saveRecipeInfoReqDTO(saveRecipeInfoReqDTO)
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

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("레시피 등록완료"))
                .andDo(print());

        verify(recipeService,times(1)).addRecipe(any(SaveRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("레시피 등록테스트 -> (실패 : 인증실패)")
    public void addRecipeInfoFail() throws Exception {
        //given
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReqDTO.class),any(Long.class));
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

        verify(recipeService,times(0)).addRecipe(any(SaveRecipeReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("레시피 등록테스트 -> (실패 : 존재하지 않는 유저)")
    public void addRecipeInfoFai3() throws Exception {
        //given
        makeAuthentication();
        Gson gson = new Gson();
        String requestBody = gson.toJson(saveRecipeReqDTO);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(recipeService).addRecipe(any(SaveRecipeReqDTO.class),any(Long.class));

        //when, then
        this.mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());

        verify(recipeService,times(1)).addRecipe(any(SaveRecipeReqDTO.class),any(Long.class));
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
        SaveRecipeInfoReqDTO saveRecipeInfoReqDTO = SaveRecipeInfoReqDTO.builder()
                .recipeInfoType(RecipeInfoType.KOREAN)
                .recipeName("")
                .recipeTip("")
                .calogy(null)
                .cookingTime(null)
                .ingredentCost(null)
                .build();

        SaveRecipeReqDTO saveRecipeReqDTO = SaveRecipeReqDTO.builder()
                .saveRecipeInfoReqDTO(saveRecipeInfoReqDTO)
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
                .andExpect(status().isBadRequest())
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
        doThrow(new CommondException(NOT_EXIST_RECIPEINFO)).when(recipeService).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));

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
    @DisplayName("레시피 수정 테스트 -> (실패 : 레시피 수정권한 없음)")
    public void updateRecipeFail7() throws Exception {
        //given
        Gson gson = new Gson();
        String requestbody = gson.toJson(saveRecipeReqDTO);
        makeAuthentication();
        doThrow(new CommondException(ExceptionCode.ACCESS_FAIL_RECIPE)).when(recipeService).updateRecipe(any(SaveRecipeReqDTO.class),any(Long.class),any(Long.class));

        //when, then
        this.mockMvc.perform(patch("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestbody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 레시피에 대해 권한이 없습니다"))
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
        doThrow(new CommondException(NOT_EXIST_RECIPEINFO)).when(recipeService).deleteRecipe(any(Long.class),any(Long.class));

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

    @Test
    @DisplayName("레시피 조회 테스트 -> 성공")
    public void readRecipeSuccess() throws Exception {
        // given
        makeAuthentication();
        ReadRecipeResDTO readRecipeResDTO = ReadRecipeResDTO.builder().build();
        given(recipeService.readRecipe(any(Long.class),any(Long.class),any())).willReturn(readRecipeResDTO);

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("레시피 상세정보 조회완료"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(print());
        verify(recipeService,times(1)).readRecipe(any(Long.class),any(Long.class),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 인증되지 않은 유저)")
    public void readRecipeFail1() throws Exception {
        // given

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any());
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
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any());
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
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 입력값enum타입 예외)")
    public void readRecipeFail4() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.sort").value("Required request parameter 'sort' for method parameter type ReplySortType is present but converted to null(관리자에게 문의하세요)"))
                .andDo(print());
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 입력값없음)")
    public void readRecipeFail5() throws Exception {
        // given
        makeAuthentication();

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.sort").value("Required request parameter 'sort' for method parameter type ReplySortType is not present(관리자에게 문의하세요)"))
                .andDo(print());
        verify(recipeService,times(0)).readRecipe(any(Long.class),any(Long.class),any());
    }

    @Test
    @DisplayName("레시피 조회 테스트 -> (실패 : 존재하지 않는 레시피)")
    public void readRecipeFail6() throws Exception {
        // given
        makeAuthentication();
        given(recipeService.readRecipe(any(Long.class),any(Long.class),any())).willThrow(new CommondException(NOT_EXIST_RECIPEINFO));

        //when, then
        this.mockMvc.perform(get("/recipes/{recipeInfoId}",1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sort", String.valueOf(ReplySortType.LIKE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 레시피입니다"))
                .andDo(print());
        verify(recipeService,times(1)).readRecipe(any(Long.class),any(Long.class),any());
    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1l,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
