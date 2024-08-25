package com.example.foodking.recipeWay.service;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWay.domain.RecipeWay;
import com.example.foodking.recipeWay.dto.request.RecipeWayAddReq;
import com.example.foodking.recipeWay.repository.RecipeWayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RecipeWayService {

    private final RecipeWayRepository recipeWayRepository;

    @Transactional
    public void addRecipeWay(List<RecipeWayAddReq> recipeWayAddReqList, RecipeInfo recipeInfo){

        List<RecipeWay> recipeWayList = recipeWayAddReqList.stream()
                .map(dto -> RecipeWayAddReq.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeWayRepository.saveAll(recipeWayList);
    }

    @Transactional
    public void updateRecipeWayList(List<RecipeWayAddReq> recipeWayAddReqList, RecipeInfo recipeInfo){

        List<RecipeWay> recipeWayList = recipeInfo.getRecipeWays();
        int minSize = Math.min(recipeWayAddReqList.size(), recipeWayList.size());

        // 기존 조리순서 업데이트
        IntStream.range(0, minSize)
                .forEach(i -> recipeWayList.get(i).updateRecipeWay(recipeWayAddReqList.get(i).getRecipeWay()));

        // 조리순서가 추가된 경우
        IntStream.range(minSize, recipeWayAddReqList.size())
                .forEach(i -> recipeWayList.add(RecipeWayAddReq.toEntity(recipeWayAddReqList.get(i), recipeInfo)));

        // 조리순서가 줄어든 경우
        IntStream.range(recipeWayAddReqList.size(), recipeWayList.size())
                .forEach(i -> recipeWayList.remove(minSize));

    }
}
