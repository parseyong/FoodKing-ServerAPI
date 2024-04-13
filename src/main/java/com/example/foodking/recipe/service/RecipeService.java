package com.example.foodking.recipe.service;

import com.example.foodking.aop.distributedLock.DistributedLock;
import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.dto.response.ReadIngredientRes;
import com.example.foodking.ingredient.service.IngredientService;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipe.request.SaveRecipeReq;
import com.example.foodking.recipe.dto.recipe.response.ReadRecipeRes;
import com.example.foodking.recipe.dto.recipeInfo.request.SaveRecipeInfoReq;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipeWayInfo.dto.response.ReadRecipeWayInfoResDTO;
import com.example.foodking.recipeWayInfo.service.RecipeWayInfoService;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.dto.response.ReadReplyRes;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeInfoRepository recipeInfoRepository;
    private final UserRepository userRepository;
    private final ReplyService replyService;
    private final EmotionService emotionService;
    private final IngredientService ingredientService;
    private final RecipeWayInfoService recipeWayInfoService;

    @Transactional
    public Long addRecipe(SaveRecipeReq saveRecipeReq, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = SaveRecipeInfoReq.toEntity(saveRecipeReq.getSaveRecipeInfoReq(),user);

        ingredientService.addIngredient(saveRecipeReq.getSaveIngredientReqList(),recipeInfo);
        recipeWayInfoService.addRecipeWay(saveRecipeReq.getSaveRecipeWayInfoReqDTOList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        return recipeInfo.getRecipeInfoId();
    }

    @Transactional
    public void updateRecipe(SaveRecipeReq saveRecipeReq, Long userId, Long recipeInfoId){

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);

        // 레시피 정보수정
        updateRecipeInfo(recipeInfo, saveRecipeReq.getSaveRecipeInfoReq());
        // 조리법 수정
        recipeWayInfoService.updateRecipeWayInfoList
                (saveRecipeReq.getSaveRecipeWayInfoReqDTOList(), recipeInfo);
        // 재료 수정
        ingredientService.updateIngredientList
                (saveRecipeReq.getSaveIngredientReqList(), recipeInfo);

        recipeInfoRepository.save(recipeInfo);
    }

    @Transactional
    public void deleteRecipe(Long userId, Long recipeInfoId){
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);
        recipeInfoRepository.delete(recipeInfo);
    }

    @DistributedLock(key = "#recipeId")
    public ReadRecipeRes readRecipe(Long userId, Long recipeInfoId, ReplySortType replySortType){

        // querydsl로 필요한 데이터를 4개의 쿼리로 모두 가져올 예정
        // 1.레시피info 정보 및 좋아요 수
        // 2.댓글 총 수와 댓글페이징결과 및 각 댓글의 좋아요 수
        // 3.재료 정보
        // 4.조리법 정보

        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);
        recipeInfo.addVisitCnt();

        Long replyCnt = (long)recipeInfo.getReplyList().size();
        Long emotionCnt = emotionService.readRecipeEmotionCnt(recipeInfo);
        User writer = recipeInfo.getUser();

        ReadRecipeInfoRes readRecipeInfoRes = ReadRecipeInfoRes.toDTO(recipeInfo,replyCnt,emotionCnt, writer.getUserId(), writer.getNickName());

        List<ReadRecipeWayInfoResDTO> readRecipeWayInfoResDTOList = recipeInfo.getRecipeWayInfoList().stream()
                .map(entity -> ReadRecipeWayInfoResDTO.toDTO(entity))
                .collect(Collectors.toList());

        List<ReadIngredientRes> readIngredientResList = recipeInfo.getIngredientList().stream()
                .map(entity -> ReadIngredientRes.toDTO(entity))
                .collect(Collectors.toList());

        List<ReadReplyRes> readReplyResList = replyService.readReply(recipeInfo,userId,replySortType);

        recipeInfoRepository.save(recipeInfo);

        return ReadRecipeRes.builder()
                .readRecipeInfoRes(readRecipeInfoRes)
                .readReplyResList(readReplyResList)
                .readRecipeWayInfoResDTOList(readRecipeWayInfoResDTOList)
                .readIngredientResList(readIngredientResList)
                .recipeTip(recipeInfo.getRecipeTip())
                .isMyRecipe(recipeInfo.getUser().getUserId() == userId)
                .visitCnt(recipeInfo.getVisitCnt())
                .build();
    }

    private void updateRecipeInfo(RecipeInfo recipeInfo, SaveRecipeInfoReq saveRecipeInfoReq){
        recipeInfo.changeCalogy(saveRecipeInfoReq.getCalogy());
        recipeInfo.changeRecipeInfoType(saveRecipeInfoReq.getRecipeInfoType());
        recipeInfo.changeCookingTime(saveRecipeInfoReq.getCookingTime());
        recipeInfo.changeIngredientCost(saveRecipeInfoReq.getIngredentCost());
        recipeInfo.changeRecipeName(saveRecipeInfoReq.getRecipeName());
        recipeInfo.changeRecipeTip(saveRecipeInfoReq.getRecipeTip());
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
