package com.example.foodking.ingredient.service;

import com.example.foodking.ingredient.domain.Ingredient;
import com.example.foodking.ingredient.dto.request.IngredientAddReq;
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
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional
    public void addIngredients(List<IngredientAddReq> ingredientAddReqs, RecipeInfo recipeInfo){

        List<Ingredient> ingredients = ingredientAddReqs.stream()
                .map(dto -> IngredientAddReq.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());

        ingredientRepository.saveAll(ingredients);
    }

    @Transactional
    public void updateIngredients(List<IngredientAddReq> ingredientAddReqs, RecipeInfo recipeInfo){

        List<Ingredient> ingredients = recipeInfo.getIngredients();
        int minSize = Math.min(ingredientAddReqs.size(), ingredients.size());

        // 기존 재료 업데이트
        IntStream.range(0,minSize)
                .forEach(i ->{
                    Ingredient ingredient = ingredients.get(i);
                    IngredientAddReq newInfo = ingredientAddReqs.get(i);
                    ingredient.updateIngredientName(newInfo.getIngredientName());
                    ingredient.updateIngredientAmount(newInfo.getIngredientAmount());
                });

        // 재료가 추가된 경우
        IntStream.range(minSize, ingredientAddReqs.size())
                .forEach(i -> {
                    Ingredient ingredient = IngredientAddReq.toEntity(ingredientAddReqs.get(i), recipeInfo);
                    ingredients.add(ingredient);
                });

        // 재료가 줄어든 경우
        IntStream.range(ingredientAddReqs.size(),ingredients.size())
                .forEach(i -> {
                    ingredients.remove(minSize);
                });
    }
}
