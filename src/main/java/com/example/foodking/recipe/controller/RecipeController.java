package com.example.foodking.recipe.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.recipe.dto.recipe.request.RecipeSaveReq;
import com.example.foodking.recipe.dto.recipeInfo.request.RecipeInfoPagingFindReq;
import com.example.foodking.recipe.enums.RecipeInfoType;
import com.example.foodking.recipe.enums.RecipeSortType;
import com.example.foodking.recipe.service.RecipePagingService;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.enums.ReplySortType;
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
    public ResponseEntity<CommonResDTO> addRecipe(final @AuthenticationPrincipal Long userId,
                                                  @RequestBody @Valid RecipeSaveReq recipeSaveReq){

        Long recipeInfoId = recipeService.addRecipe(recipeSaveReq,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",recipeInfoId));
    }

    @PatchMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> updateRecipe(final @AuthenticationPrincipal Long userId,
                                                     @RequestBody @Valid RecipeSaveReq recipeSaveReq,
                                                     final @PathVariable Long recipeInfoId){

        recipeService.updateRecipe(recipeSaveReq, userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 수정완료",null));
    }

    @DeleteMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteRecipe(final @AuthenticationPrincipal Long userId,
                                                     final @PathVariable Long recipeInfoId){

        recipeService.deleteRecipe(userId,recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 삭제완료",null));
    }

    @GetMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> findRecipe(final @AuthenticationPrincipal Long userId,
                                                   final @PathVariable Long recipeInfoId,
                                                   final @RequestParam(name = "sort") ReplySortType replySortType,
                                                   final @RequestParam(name = "lastId", required = false) Long lastId,
                                                   final @RequestParam(name = "lastValue", required = false) Object lastValue){

        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 상세정보 조회완료",
                        recipeService.findRecipe(userId,recipeInfoId,replySortType, lastId, lastValue)));
    }

    @GetMapping("/recipes/{recipeType}/list")
    public ResponseEntity<CommonResDTO> findRecipeInfoPagingByType(final @AuthenticationPrincipal Long userId,
                                                                   final @PathVariable(name = "recipeType") RecipeInfoType recipeInfoType,
                                                                   final @RequestParam RecipeSortType recipeSortType,
                                                                   final @RequestParam(required = false) String searchKeyword,
                                                                   final @RequestParam(name = "lastId", required = false) Long lastId,
                                                                   final @RequestParam(name = "lastValue", required = false) Object lastValue){

        RecipeInfoPagingFindReq recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .condition(recipeInfoType)
                .userId(userId)
                .lastId(lastId)
                .lastValue(lastValue)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 타입 조회성공",
                recipePagingService.findRecipeInfoPagingByCondition(recipeInfoPagingFindReq)));
    }

    @GetMapping("/recipes/mine/list")
    public ResponseEntity<CommonResDTO> findMyRecipeInfoPaging(final @AuthenticationPrincipal Long userId,
                                                               final @RequestParam RecipeSortType recipeSortType,
                                                               final @RequestParam(required = false) String searchKeyword,
                                                               final @RequestParam(name = "lastId", required = false) Long lastId,
                                                               final @RequestParam(name = "lastValue", required = false) Object lastValue){

        RecipeInfoPagingFindReq recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .condition("mine")
                .userId(userId)
                .lastId(lastId)
                .lastValue(lastValue)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("내가 쓴 레시피 조회성공",
                recipePagingService.findRecipeInfoPagingByCondition(recipeInfoPagingFindReq)));
    }

    @GetMapping("/recipes/like/list")
    public ResponseEntity<CommonResDTO> findLikeRecipeInfoPaging(final @AuthenticationPrincipal Long userId,
                                                                 final @RequestParam RecipeSortType recipeSortType,
                                                                 final @RequestParam(required = false) String searchKeyword,
                                                                 final @RequestParam(name = "lastId", required = false) Long lastId,
                                                                 final @RequestParam(name = "lastValue", required = false) Object lastValue){

        RecipeInfoPagingFindReq recipeInfoPagingFindReq = RecipeInfoPagingFindReq.builder()
                .recipeSortType(recipeSortType)
                .searchKeyword(searchKeyword)
                .condition("like")
                .userId(userId)
                .lastId(lastId)
                .lastValue(lastValue)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("좋아요 누른 레시피 조회성공",
                recipePagingService.findLikedRecipeInfoPaging(recipeInfoPagingFindReq)));
    }

}
