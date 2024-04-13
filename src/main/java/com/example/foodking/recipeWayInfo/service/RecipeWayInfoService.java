package com.example.foodking.recipeWayInfo.service;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipeWayInfo.domain.RecipeWayInfo;
import com.example.foodking.recipeWayInfo.dto.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipeWayInfo.repository.RecipeWayInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeWayInfoService {

    private final RecipeWayInfoRepository recipeWayInfoRepository;

    @Transactional
    public void addRecipeWay(List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList, RecipeInfo recipeInfo){

        List<RecipeWayInfo> recipeWayInfoList = saveRecipeWayInfoReqDTOList.stream()
                .map(dto -> SaveRecipeWayInfoReqDTO.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeWayInfoRepository.saveAll(recipeWayInfoList);
    }

    @Transactional
    public void updateRecipeWayInfoList(
            List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList , RecipeInfo recipeInfo){

        List<RecipeWayInfo> recipeWayInfoList = recipeInfo.getRecipeWayInfoList();
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
}
