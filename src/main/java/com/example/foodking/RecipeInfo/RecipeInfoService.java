package com.example.foodking.RecipeInfo;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.Ingredient.IngredientRepository;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import com.example.foodking.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeInfoService {

    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeWayInfoRepository recipeWayInfoRepository;

    @Transactional
    public void addRecipeInfo(AddRecipeReqDTO addRecipeReqDTO,Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = AddRecipeReqDTO.toRecipeInfoEntity(addRecipeReqDTO,user);
        List<Ingredient> ingredientList = AddRecipeReqDTO.toIngredientListEntity(addRecipeReqDTO.getAddIngredientReqDTOList(),recipeInfo);
        List<RecipeWayInfo> recipeWayInfoList = AddRecipeReqDTO.toRecipeWayInfoListEntity(addRecipeReqDTO.getAddRecipeWayInfoReqDTOList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
    }
}
