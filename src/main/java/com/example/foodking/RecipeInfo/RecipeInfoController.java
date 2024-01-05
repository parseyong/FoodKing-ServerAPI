package com.example.foodking.RecipeInfo;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeInfo")
public class RecipeInfoController {

    private final RecipeInfoService recipeInfoService;


    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(@RequestBody @Valid AddRecipeReqDTO addRecipeReqDTO){

        Long userId = JwtProvider.getUserId();
        Long recipeInfoId = recipeInfoService.addRecipeInfo(addRecipeReqDTO,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",recipeInfoId));
    }

    @PostMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> addImage(@RequestParam(name = "recipeImage") MultipartFile recipeImage,
                                                 @PathVariable Long recipeInfoId) {

        recipeInfoService.addImage(recipeImage,recipeInfoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("이미지 등록완료",null));
    }

    @DeleteMapping ("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteImage(@PathVariable Long recipeInfoId) {

        recipeInfoService.deleteImage(recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이미지 삭제완료",null));
    }
}
