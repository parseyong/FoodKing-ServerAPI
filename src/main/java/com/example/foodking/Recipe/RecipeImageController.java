package com.example.foodking.Recipe;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeImage")
public class RecipeImageController {

    private final RecipeImageService recipeImageService;

    @PostMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> saveImage(@RequestParam(name = "recipeImage") MultipartFile recipeImage,
                                                  @PathVariable final Long recipeInfoId) {
        final Long userId = JwtProvider.getUserId();
        recipeImageService.saveImage(recipeImage,recipeInfoId,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("이미지 등록완료",null));
    }

    @DeleteMapping("/recipes/images/{recipeInfoId}")
    public ResponseEntity<CommonResDTO> deleteImage(@PathVariable final Long recipeInfoId) {
        final Long userId = JwtProvider.getUserId();
        recipeImageService.deleteImage(recipeInfoId,userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이미지 삭제완료",null));
    }
}
