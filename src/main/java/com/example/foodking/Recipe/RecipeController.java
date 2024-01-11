package com.example.foodking.Recipe;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.Recipe.DTO.SaveRecipeReqDTO;
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
public class RecipeController {

    private final RecipeService recipeService;


    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(@RequestBody @Valid SaveRecipeReqDTO saveRecipeReqDTO){

        Long userId = JwtProvider.getUserId();
        Long recipeInfoId = recipeService.addRecipe(saveRecipeReqDTO,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",recipeInfoId));
    }

    @PostMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> addImage(@RequestParam(name = "recipeImage") MultipartFile recipeImage,
                                                 @PathVariable Long recipeInfoId) {

        recipeService.addImage(recipeImage,recipeInfoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("이미지 등록완료",null));
    }

    @DeleteMapping ("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteImage(@PathVariable Long recipeInfoId) {

        recipeService.deleteImage(recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이미지 삭제완료",null));
    }

    @PatchMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> updateRecipe(@RequestBody @Valid SaveRecipeReqDTO saveRecipeReqDTO ,@PathVariable Long recipeInfoId){

        recipeService.updateRecipe(saveRecipeReqDTO,JwtProvider.getUserId(),recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 수정완료",null));
    }

    @DeleteMapping("/recipes/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteRecipe(@PathVariable Long recipeInfoId){
        recipeService.deleteRecipe(JwtProvider.getUserId(),recipeInfoId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피 삭제완료",null));
    }
}
