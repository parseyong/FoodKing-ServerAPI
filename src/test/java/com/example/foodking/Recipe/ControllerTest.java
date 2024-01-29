package com.example.foodking.Recipe;

import com.example.foodking.Auth.CustomUserDetailsService;
import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Config.SecurityConfig;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.SaveRecipeReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.SaveIngredientReqDTO;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoType;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.SaveRecipeWayInfoReqDTO;
import com.example.foodking.User.User;
import com.example.foodking.User.UserService;
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

        verify(userService,times(1)).findUserById(any(Long.class));
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
}
