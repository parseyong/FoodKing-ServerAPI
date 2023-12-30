package com.example.foodking.RecipeInfo;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeInfo")
public class RecipeInfoController {

    private final RecipeInfoService recipeInfoService;
    private final JwtProvider jwtProvider;

    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(@RequestBody @Valid AddRecipeReqDTO addRecipeReqDTO, HttpServletRequest servletRequest){

        Long userId = jwtProvider.readUserIdByToken(servletRequest);
        recipeInfoService.addRecipeInfo(addRecipeReqDTO,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",null));
    }
}
