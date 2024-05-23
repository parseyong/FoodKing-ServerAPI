package com.example.foodking.recipe.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReq;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeRes;
import com.example.foodking.recipe.dto.recipeInfo.request.ReadRecipeInfoPagingReq;
import com.example.foodking.recipe.service.RecipePagingService;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.common.ReplySortType;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeInfo")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipePagingService recipePagingService;

    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(
            @AuthenticationPrincipal final Long userId,
            @RequestBody @Valid SaveRecipeReq saveRecipeReq){

        Long recipeInfoId = recipeService.addRecipe(saveRecipeReq,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",recipeInfoId));
    }

    @PatchMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> updateRecipe(
            @AuthenticationPrincipal final Long userId,
            @RequestBody @Valid SaveRecipeReq saveRecipeReq,
            @PathVariable final Long recipeInfoId){

        recipeService.updateRecipe(saveRecipeReq, userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 수정완료",null));
    }

    @DeleteMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteRecipe(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long recipeInfoId){

        recipeService.deleteRecipe(userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 삭제완료",null));
    }

    @GetMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> readRecipe(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long recipeInfoId,
            @RequestParam(name = "sort") ReplySortType replySortType,
            @RequestParam(name = "lastId") Long lastId,
            @RequestParam(name = "lastValue") Object lastValue){

        ReadRecipeRes readRecipeRes = recipeService.readRecipe(userId,recipeInfoId,replySortType, lastId,lastValue);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 상세정보 조회완료", readRecipeRes));
    }

    @GetMapping("/recipes/{recipeType}/{pageNum}")
    public ResponseEntity<CommonResDTO> readRecipeInfoPagingByType(
            @AuthenticationPrincipal final Long userId,
            @PathVariable(name = "recipeType") final RecipeInfoType recipeInfoType,
            @PathVariable final Long pageNum,
            @RequestParam RecipeSortType recipeSortType,
            @RequestParam(required = false) String searchKeyword){

        ReadRecipeInfoPagingReq readRecipeInfoPagingReq = ReadRecipeInfoPagingReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .pageNum(pageNum)
                .condition(recipeInfoType)
                .userId(userId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 타입 조회성공",
                recipePagingService.readRecipeInfoPagingByCondition(readRecipeInfoPagingReq)));
    }

    @GetMapping("/recipes/mine/{pageNum}")
    public ResponseEntity<CommonResDTO> readMyRecipeInfoPaging(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long pageNum,
            @RequestParam RecipeSortType recipeSortType,
            @RequestParam(required = false) String searchKeyword){

        ReadRecipeInfoPagingReq readRecipeInfoPagingReq = ReadRecipeInfoPagingReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .pageNum(pageNum)
                .condition("mine")
                .userId(userId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("내가 쓴 레시피 조회성공",
                recipePagingService.readRecipeInfoPagingByCondition(readRecipeInfoPagingReq)));
    }

    @GetMapping("/recipes/like/{pageNum}")
    public ResponseEntity<CommonResDTO> readLikeRecipeInfoPaging(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long pageNum,
            @RequestParam RecipeSortType recipeSortType,
            @RequestParam(required = false) String searchKeyword){

        ReadRecipeInfoPagingReq readRecipeInfoPagingReq = ReadRecipeInfoPagingReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .pageNum(pageNum)
                .condition("like")
                .userId(userId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("좋아요 누른 레시피 조회성공",
                recipePagingService.readLikedRecipeInfoPaging(readRecipeInfoPagingReq)));
    }

}
