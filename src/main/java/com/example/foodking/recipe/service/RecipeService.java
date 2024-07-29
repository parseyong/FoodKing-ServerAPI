package com.example.foodking.recipe.service;

import com.example.foodking.aop.distributedLock.DistributedLock;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.ingredient.service.IngredientService;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipe.request.RecipeSaveReq;
import com.example.foodking.recipe.dto.recipe.response.RecipeFindRes;
import com.example.foodking.recipe.dto.recipeInfo.request.RecipeInfoSaveReq;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipeWay.service.RecipeWayService;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.dto.response.ReplyFindRes;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeInfoRepository recipeInfoRepository;
    private final UserRepository userRepository;
    private final ReplyService replyService;
    private final IngredientService ingredientService;
    private final RecipeWayService recipeWayService;
    private final RecipeCachingService recipeCachingService;

    @Transactional
    public Long addRecipe(RecipeSaveReq recipeSaveReq, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = RecipeInfoSaveReq.toEntity(recipeSaveReq.getRecipeInfoSaveReq(),user);

        ingredientService.addIngredient(recipeSaveReq.getIngredientAddReqList(),recipeInfo);
        recipeWayService.addRecipeWay(recipeSaveReq.getRecipeWayAddReqList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        return recipeInfo.getRecipeInfoId();
    }

    @Transactional
    @CacheEvict(value = "recipeInfoCache", key = "#recipeInfoId", cacheManager = "redisCacheManager")
    public void updateRecipe(RecipeSaveReq recipeSaveReq, Long userId, Long recipeInfoId){

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);

        // 레시피 정보수정
        updateRecipeInfo(recipeInfo, recipeSaveReq.getRecipeInfoSaveReq());

        // 조리법 수정
        recipeWayService.updateRecipeWayList( recipeSaveReq.getRecipeWayAddReqList(), recipeInfo );

        // 재료 수정
        ingredientService.updateIngredientList( recipeSaveReq.getIngredientAddReqList(), recipeInfo );

        recipeInfoRepository.save(recipeInfo);
    }

    @Transactional
    @CacheEvict(value = "recipeInfoCache", key = "#recipeInfoId", cacheManager = "redisCacheManager")
    public void deleteRecipe(Long userId, Long recipeInfoId){
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_RECIPE);
        recipeInfoRepository.delete(recipeInfo);
    }

    @DistributedLock(key = "#LockRecipe")
    public Object findRecipe(Long userId, Long recipeInfoId,
                             ReplySortType replySortType,
                             Long lastId, Object lastValue){

        // 만약 첫번째 페이지를 요청했다면 레시피정보를 가져와야하지만
        // 첫번째 페이지가 아니라면 레시피정보를 가져올 필요가 없이 댓글정보만 가져오면 된다.
        if(lastId != null && lastValue != null)
            return replyService.findReplyList(recipeInfoId, userId, replySortType, lastId, lastValue, false);

        RecipeFindRes recipeFindRes = (RecipeFindRes) recipeCachingService.findRecipeByCache(recipeInfoId,true);

        // 댓글 페이징 조회
        List<ReplyFindRes> replyFindResList = replyService
                .findReplyList(recipeInfoId, userId, replySortType, lastId, lastValue,true);

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        recipeInfo.addVisitCnt();
        recipeInfoRepository.save(recipeInfo);

        return RecipeFindRes.builder()
                .recipeInfoFindRes(recipeFindRes.getRecipeInfoFindRes())
                .replyFindResList(replyFindResList)
                .recipeWayFindResList(recipeFindRes.getRecipeWayFindResList())
                .ingredientFindResList(recipeFindRes.getIngredientFindResList())
                .isMyRecipe(recipeInfo.getUser().getUserId() == userId)
                .build();
    }

    public static void isMyRecipe(Long userId, User user, ExceptionCode exceptionCode){
        if( user ==null || !userId.equals(user.getUserId()) )
            throw new CommondException(exceptionCode);
    }

    private void updateRecipeInfo(RecipeInfo recipeInfo, RecipeInfoSaveReq recipeInfoSaveReq){

        recipeInfo.updateCalogy(recipeInfoSaveReq.getCalogy());
        recipeInfo.updateRecipeInfoType(recipeInfoSaveReq.getRecipeInfoType());
        recipeInfo.updateCookingTime(recipeInfoSaveReq.getCookingTime());
        recipeInfo.updateIngredientCost(recipeInfoSaveReq.getIngredentCost());
        recipeInfo.updateRecipeName(recipeInfoSaveReq.getRecipeName());
        recipeInfo.updateRecipeTip(recipeInfoSaveReq.getRecipeTip());
    }

}
