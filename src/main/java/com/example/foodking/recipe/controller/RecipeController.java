package com.example.foodking.recipe.controller;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.common.CommonResDTO;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReqDTO;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeResDTO;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.common.ReplySortType;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeInfo")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(@RequestBody @Valid SaveRecipeReqDTO saveRecipeReqDTO){

        final Long userId = JwtProvider.getUserId();
        Long recipeInfoId = recipeService.addRecipe(saveRecipeReqDTO,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",recipeInfoId));
    }

    @PatchMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> updateRecipe(@RequestBody @Valid SaveRecipeReqDTO saveRecipeReqDTO ,@PathVariable final Long recipeInfoId){

        final Long userId = JwtProvider.getUserId();
        recipeService.updateRecipe(saveRecipeReqDTO, userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 수정완료",null));
    }

    @DeleteMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteRecipe(@PathVariable final Long recipeInfoId){
        final Long userId = JwtProvider.getUserId();
        recipeService.deleteRecipe(userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 삭제완료",null));
    }

    @GetMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> readRecipe(@PathVariable final Long recipeInfoId,
                                                   @RequestParam(name = "sort") @NotNull(message ="정렬타입을 입력해주세요") ReplySortType replySortType){
        final Long userId = JwtProvider.getUserId();
        ReadRecipeResDTO readRecipeResDTO = recipeService.readRecipe(userId,recipeInfoId,replySortType);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 상세정보 조회완료",readRecipeResDTO));
    }

    @GetMapping("/recipes/{recipeType}/{pageNum}")
    public ResponseEntity<CommonResDTO> paging(@PathVariable(name = "recipeType") final RecipeInfoType recipeInfoType,
                                    @PathVariable final Long pageNum, @RequestParam(name = "sort") @NotNull(message ="정렬타입을 입력해주세요")RecipeSortType recipeSortType){

        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 조회성공"
                ,recipeService.readRecipePaging(pageNum,recipeSortType,recipeInfoType)));
    }
}
