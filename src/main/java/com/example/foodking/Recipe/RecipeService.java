package com.example.foodking.Recipe;

import com.example.foodking.Emotion.EmotionService;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.Request.SaveRecipeReqDTO;
import com.example.foodking.Recipe.DTO.Response.ReadRecipeResDTO;
import com.example.foodking.Recipe.Ingredient.DTO.Request.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.Response.ReadIngredientResDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.Ingredient.IngredientRepository;
import com.example.foodking.Recipe.RecipeInfo.DTO.Request.SaveRecipeInfoReqDTO;
import com.example.foodking.Recipe.RecipeInfo.DTO.Response.ReadRecipeInfoResDTO;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.Request.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.Response.ReadRecipeWayInfoResDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.Reply.DTO.Response.ReadReplyResDTO;
import com.example.foodking.Reply.ReplyService;
import com.example.foodking.Reply.ReplySortType;
import com.example.foodking.User.User;
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
    private final ReplyService replyService;
    private final EmotionService emotionService;

    @Transactional
    public Long addRecipe(SaveRecipeReqDTO saveRecipeReqDTO, User user){
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

        ReadRecipeInfoResDTO readRecipeInfoResDTO = ReadRecipeInfoResDTO.toDTO(recipeInfo);

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
                .replyCnt((long) recipeInfo.getReplyList().size())
                .emotionCnt(emotionService.readRecipeEmotionCnt(recipeInfo))
                .regDate(recipeInfo.getRegDate())
                .modDate(recipeInfo.getModDate())
                .isMyRecipe(recipeInfo.getUser().getUserId() == userId)
                .build();
    }

    public void updateRecipeInfo(RecipeInfo recipeInfo, SaveRecipeInfoReqDTO saveRecipeInfoReqDTO){
        recipeInfo.changeCalogy(saveRecipeInfoReqDTO.getCalogy());
        recipeInfo.changeRecipeInfoType(saveRecipeInfoReqDTO.getRecipeInfoType());
        recipeInfo.changeCookingTime(saveRecipeInfoReqDTO.getCookingTime());
        recipeInfo.changeIngredientCost(saveRecipeInfoReqDTO.getIngredentCost());
        recipeInfo.changeRecipeName(saveRecipeInfoReqDTO.getRecipeName());
        recipeInfo.changeRecipeTip(saveRecipeInfoReqDTO.getRecipeTip());
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

    public static void isMyRecipe(Long userId, User user, ExceptionCode exceptionCode){
        if( user ==null || !userId.equals(user.getUserId()) )
            throw new CommondException(exceptionCode);
    }
}
