package com.example.foodking.recipe.service;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.Ingredient;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.domain.RecipeWayInfo;
import com.example.foodking.recipe.dto.ingredient.request.SaveIngredientReqDTO;
import com.example.foodking.recipe.dto.ingredient.response.ReadIngredientResDTO;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReqDTO;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeResDTO;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReqDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.recipe.dto.recipeWayInfo.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.recipe.repository.IngredientRepository;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.repository.RecipeWayInfoRepository;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.dto.response.ReadReplyResDTO;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class RecipeService {

    private final RecipeInfoRepository recipeInfoRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeWayInfoRepository recipeWayInfoRepository;
    private final UserRepository userRepository;
    private final ReplyService replyService;
    private final EmotionService emotionService;

    @Transactional
    public Long addRecipe(SaveRecipeReqDTO saveRecipeReqDTO, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = SaveRecipeInfoReqDTO.toEntity(saveRecipeReqDTO.getSaveRecipeInfoReqDTO(),user);

        List<Ingredient> ingredientList = saveRecipeReqDTO.getSaveIngredientReqDTOList().stream()
                .map(dto -> SaveIngredientReqDTO.toEntity(dto, recipeInfo))
                .collect(Collectors.toList());

        List<RecipeWayInfo> recipeWayInfoList = saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList().stream()
                .map(dto -> SaveRecipeWayInfoReqDTO.toEntity(dto,recipeInfo))
                .collect(Collectors.toList());

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
        return  recipeInfo.getRecipeInfoId();
    }

    @Transactional
    public void updateRecipe(SaveRecipeReqDTO saveRecipeReqDTO, Long userId,Long recipeInfoId){

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);

        List<Ingredient> ingredientList = recipeInfo.getIngredientList();
        List<RecipeWayInfo> recipeWayInfoList = recipeInfo.getRecipeWayInfoList();

        updateRecipeInfo(recipeInfo,saveRecipeReqDTO.getSaveRecipeInfoReqDTO());
        updateRecipeWayInfoList(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeWayInfoList,recipeInfo);
        updateIngredientList(saveRecipeReqDTO.getSaveIngredientReqDTOList(),ingredientList,recipeInfo);

        recipeInfoRepository.save(recipeInfo);
    }

    @Transactional
    public void deleteRecipe(Long userId, Long recipeInfoId){
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);
        recipeInfoRepository.delete(recipeInfo);
    }

    public ReadRecipeResDTO readRecipe(Long userId,Long recipeInfoId, ReplySortType replySortType){
        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);

        Long replyCnt = (long)recipeInfo.getReplyList().size();
        Long emotionCnt = emotionService.readRecipeEmotionCnt(recipeInfo);

        ReadRecipeInfoResDTO readRecipeInfoResDTO = ReadRecipeInfoResDTO.toDTO(recipeInfo,replyCnt,emotionCnt);

        List<ReadRecipeWayInfoResDTO> readRecipeWayInfoResDTOList = recipeInfo.getRecipeWayInfoList().stream()
                .map(entity -> ReadRecipeWayInfoResDTO.toDTO(entity))
                .collect(Collectors.toList());

        List<ReadIngredientResDTO> readIngredientResDTOList = recipeInfo.getIngredientList().stream()
                .map(entity -> ReadIngredientResDTO.toDTO(entity))
                .collect(Collectors.toList());

        List<ReadReplyResDTO> readReplyResDTOList = replyService.readReply(recipeInfo,userId,replySortType);

        return ReadRecipeResDTO.builder()
                .readRecipeInfoResDTO(readRecipeInfoResDTO)
                .readReplyResDTOList(readReplyResDTOList)
                .readRecipeWayInfoResDTOList(readRecipeWayInfoResDTOList)
                .readIngredientResDTOList(readIngredientResDTOList)
                .recipeTip(recipeInfo.getRecipeTip())
                .isMyRecipe(recipeInfo.getUser().getUserId() == userId)
                .build();
    }

    private void updateRecipeInfo(RecipeInfo recipeInfo, SaveRecipeInfoReqDTO saveRecipeInfoReqDTO){
        recipeInfo.changeCalogy(saveRecipeInfoReqDTO.getCalogy());
        recipeInfo.changeRecipeInfoType(saveRecipeInfoReqDTO.getRecipeInfoType());
        recipeInfo.changeCookingTime(saveRecipeInfoReqDTO.getCookingTime());
        recipeInfo.changeIngredientCost(saveRecipeInfoReqDTO.getIngredentCost());
        recipeInfo.changeRecipeName(saveRecipeInfoReqDTO.getRecipeName());
        recipeInfo.changeRecipeTip(saveRecipeInfoReqDTO.getRecipeTip());
    }

    private void updateRecipeWayInfoList(List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList , List<RecipeWayInfo> recipeWayInfoList,
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

    private void updateIngredientList(List<SaveIngredientReqDTO> saveIngredientReqDTOList, List<Ingredient> ingredientList,
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

    private RecipeInfo findRecipeInfoById(Long recipeInfoId){
        return recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));
    }

    public static void isMyRecipe(Long userId, User user, ExceptionCode exceptionCode){
        if( user ==null || !userId.equals(user.getUserId()) )
            throw new CommondException(exceptionCode);
    }
}
