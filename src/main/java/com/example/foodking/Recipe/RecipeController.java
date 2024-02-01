package com.example.foodking.Recipe;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.Recipe.DTO.Request.SaveRecipeReqDTO;
import com.example.foodking.Recipe.DTO.Response.ReadRecipeResDTO;
import com.example.foodking.Reply.ReplySortType;
import com.example.foodking.User.UserService;
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
    private final UserService userService;

    @PostMapping("/recipes")
    public ResponseEntity<CommonResDTO> addRecipe(@RequestBody @Valid SaveRecipeReqDTO saveRecipeReqDTO){

        final Long userId = JwtProvider.getUserId();
        Long recipeInfoId = recipeService.addRecipe(saveRecipeReqDTO,userService.findUserById(userId));
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
}
