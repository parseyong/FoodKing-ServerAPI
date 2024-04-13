package com.example.foodking.ingredient.service;

import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.ingredient.dto.request.SaveIngredientReq;
import com.example.foodking.ingredient.repository.IngredientRepository;
import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional
    public void addIngredient(List<SaveIngredientReq> saveIngredientReqList, RecipeInfo recipeInfo){

        List<Ingredient> ingredientList = saveIngredientReqList.stream()
                .map(dto -> SaveIngredientReq.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());

        ingredientRepository.saveAll(ingredientList);
    }

    @Transactional
    public void updateIngredientList(
            List<SaveIngredientReq> saveIngredientReqList, RecipeInfo recipeInfo){

        List<Ingredient> ingredientList = recipeInfo.getIngredientList();
        int minSize = Math.min(saveIngredientReqList.size(), ingredientList.size());

        // 기존 재료 업데이트
        IntStream.range(0,minSize)
                .forEach(i ->{
                    Ingredient ingredient = ingredientList.get(i);
                    SaveIngredientReq newInfo = saveIngredientReqList.get(i);
                    ingredient.changeIngredientName(newInfo.getIngredientName());
                    ingredient.changeIngredientAmount(newInfo.getIngredientAmount());
                });

        // 재료가 추가된 경우
        IntStream.range(minSize, saveIngredientReqList.size())
                .forEach(i -> {
                    Ingredient ingredient = SaveIngredientReq.toEntity(saveIngredientReqList.get(i),recipeInfo);
                    ingredientList.add(ingredient);
                });

        // 재료가 줄어든 경우
        IntStream.range(saveIngredientReqList.size(),ingredientList.size())
                .forEach(i -> {
                    ingredientList.remove(minSize);
                });
    }
}
