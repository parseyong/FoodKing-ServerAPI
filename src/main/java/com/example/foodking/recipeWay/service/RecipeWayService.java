package com.example.foodking.recipeWay.service;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWay.domain.RecipeWay;
import com.example.foodking.recipeWay.dto.request.RecipeWayAddReq;
import com.example.foodking.recipeWay.repository.RecipeWayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RecipeWayService {

    private final RecipeWayRepository recipeWayRepository;

    @Transactional
    public void addRecipeWays(List<RecipeWayAddReq> recipeWayAddReqs, RecipeInfo recipeInfo){

        List<RecipeWay> recipeWays = recipeWayAddReqs.stream()
                .map(dto -> RecipeWayAddReq.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeWayRepository.saveAll(recipeWays);
    }

    @Transactional
    public void updateRecipeWays(List<RecipeWayAddReq> recipeWayAddReqs, RecipeInfo recipeInfo){

        List<RecipeWay> recipeWays = recipeWayRepository.findAllByRecipeInfo(recipeInfo);
        List<RecipeWay> deletedRecipeWays = new ArrayList<>();
        int minSize = Math.min(recipeWayAddReqs.size(), recipeWays.size());

        // 기존 조리순서 업데이트
        IntStream.range(0, minSize)
                .forEach(i -> recipeWays.get(i).updateRecipeWay(recipeWayAddReqs.get(i).getRecipeWay()));

        // 조리순서가 추가된 경우
        IntStream.range(minSize, recipeWayAddReqs.size())
                .forEach(i -> recipeWays.add(RecipeWayAddReq.toEntity(recipeWayAddReqs.get(i), recipeInfo)));

        // 조리순서가 줄어든 경우
        IntStream.range(recipeWayAddReqs.size(), recipeWays.size())
                .forEach(i -> {
                    deletedRecipeWays.add(recipeWays.get(minSize));
                    recipeWays.remove(minSize);
                });

        recipeWayRepository.deleteAll(deletedRecipeWays);
        recipeWayRepository.saveAll(recipeWays);
    }
}
