package com.example.foodking.RecipeInfo;

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

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(value = "RecipeInfo")
public class RecipeInfoController {

    @PostMapping("/recipes")
    public ResponseEntity<?> addRecipe(@RequestBody @Valid AddRecipeReqDTO addRecipeReqDTO){

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("레시피 등록완료",null));
    }
}
