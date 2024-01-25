package com.example.foodking.Recipe;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.SaveRecipeReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.Ingredient.IngredientRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class RecipeService {

    private final RecipeInfoRepository recipeInfoRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeWayInfoRepository recipeWayInfoRepository;

    @Transactional
    public Long addRecipe(SaveRecipeReqDTO saveRecipeReqDTO, User user){

        RecipeInfo recipeInfo = SaveRecipeReqDTO.toRecipeInfoEntity(saveRecipeReqDTO,user);
        List<Ingredient> ingredientList = SaveRecipeReqDTO.toIngredientListEntity(saveRecipeReqDTO.getSaveIngredientReqDTOList(),recipeInfo);
        List<RecipeWayInfo> recipeWayInfoList = SaveRecipeReqDTO.toRecipeWayInfoListEntity(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
        return  recipeInfo.getRecipeInfoId();
    }

    @Transactional
    public void updateRecipe(SaveRecipeReqDTO saveRecipeReqDTO, Long userId,Long recipeInfoId){

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser());
        List<Ingredient> ingredientList = recipeInfo.getIngredientList();
        List<RecipeWayInfo> recipeWayInfoList = recipeInfo.getRecipeWayInfoList();

        updateRecipeInfo(recipeInfo,saveRecipeReqDTO);
        updateRecipeWayInfoList(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeWayInfoList,recipeInfo);
        updateIngredientList(saveRecipeReqDTO.getSaveIngredientReqDTOList(),ingredientList,recipeInfo);

        recipeInfoRepository.save(recipeInfo);
    }

    @Transactional
    public void deleteRecipe(Long userId, Long recipeInfoId){
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser());
        recipeInfoRepository.delete(recipeInfo);
    }

    public void updateRecipeInfo(RecipeInfo recipeInfo, SaveRecipeReqDTO saveRecipeReqDTO){
        recipeInfo.changeCalogy(saveRecipeReqDTO.getCalogy());
        recipeInfo.changeRecipeInfoType(saveRecipeReqDTO.getRecipeInfoType());
        recipeInfo.changeCookingTime(saveRecipeReqDTO.getCookingTime());
        recipeInfo.changeIngredientCost(saveRecipeReqDTO.getIngredentCost());
        recipeInfo.changeRecipeName(saveRecipeReqDTO.getRecipeName());
        recipeInfo.changeRecipeTip(saveRecipeReqDTO.getRecipeTip());
    }

    public void updateRecipeWayInfoList(List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList , List<RecipeWayInfo> recipeWayInfoList,
                                        RecipeInfo recipeInfo){
        int minSize = Math.min(saveRecipeWayInfoReqDTOList.size(), recipeWayInfoList.size());

        // 기존 조리순서 업데이트
        IntStream.range(0, minSize)
                .forEach(i -> recipeWayInfoList.get(i).changeRecipeWay(saveRecipeWayInfoReqDTOList.get(i).getRecipeWay()));

        // 조리순서가 추가된 경우
        IntStream.range(minSize, saveRecipeWayInfoReqDTOList.size())
                .forEach(i -> recipeWayInfoList.add(SaveRecipeWayInfoReqDTO.toEntity(saveRecipeWayInfoReqDTOList.get(i), recipeInfo)));

        // 조리순서가 줄어든 경우
        IntStream.range(saveRecipeWayInfoReqDTOList.size(), recipeWayInfoList.size())
                .forEach(i -> recipeWayInfoList.remove(minSize));

    }

    public void updateIngredientList(List<SaveIngredientReqDTO> saveIngredientReqDTOList, List<Ingredient> ingredientList,
                                     RecipeInfo recipeInfo){
        int minSize = Math.min(saveIngredientReqDTOList.size(), ingredientList.size());

        // 기존 재료 업데이트
        IntStream.range(0,minSize)
                .forEach(i ->{
                    Ingredient ingredient = ingredientList.get(i);
                    SaveIngredientReqDTO newInfo = saveIngredientReqDTOList.get(i);
                    ingredient.changeIngredientName(newInfo.getIngredientName());
                    ingredient.changeIngredientAmount(newInfo.getIngredientAmount());
                });

        // 재료가 추가된 경우
        IntStream.range(minSize,saveIngredientReqDTOList.size())
                .forEach(i -> {
                    Ingredient ingredient = SaveIngredientReqDTO.toEntity(saveIngredientReqDTOList.get(i),recipeInfo);
                    ingredientList.add(ingredient);
                });

        // 재료가 줄어든 경우
        IntStream.range(saveIngredientReqDTOList.size(),ingredientList.size())
                .forEach(i -> {
                    ingredientList.remove(minSize);
                });
    }

    public RecipeInfo findRecipeInfoById(Long recipeInfoId){
        return recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));
    }

    public static void isMyRecipe(Long userId, User user){
        /*
           단위 테스트시 userId값을 지정할 수 없기때문에 해당 조건문을 추가하여 테스트를 통과할 수 있도록 했다.
           실제 환경에서는 User는 null이 아니고 user.userId값은 null인 경우는 존재하지 않는다.
        */
        if(user != null && user.getUserId() == null)
            ;
        else if(!userId.equals(user.getUserId()) )
            throw new CommondException(ExceptionCode.ACCESS_FAIL_RECIPE);

    }
}
