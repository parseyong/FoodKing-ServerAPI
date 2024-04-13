package com.example.foodking.recipe.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.dto.recipeInfo.request.ReadRecipeInfoPagingReq;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoPagingRes;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodking.emotion.domain.QRecipeEmotion.recipeEmotion;
import static com.example.foodking.recipe.domain.QRecipeInfo.recipeInfo;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipePagingService {

    private final RecipeInfoRepository recipeInfoRepository;

    public ReadRecipeInfoPagingRes readRecipeInfoPagingByCondition(ReadRecipeInfoPagingReq readRecipeInfoPagingReq){

        Object condition = readRecipeInfoPagingReq.getCondition();
        String searchKeyword = readRecipeInfoPagingReq.getSearchKeyword();
        Long userId = readRecipeInfoPagingReq.getUserId();
        RecipeSortType recipeSortType = readRecipeInfoPagingReq.getRecipeSortType();
        Pageable pageable= PageRequest.of((int) (readRecipeInfoPagingReq.getPageNum()-1),10);

        // 조건(condition)에 따라 동적으로 WHERE절을 생성
        BooleanBuilder builder = getBuilder(condition,searchKeyword,userId);

        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = recipeInfoRepository.findRecipeInfoTotalCnt(builder);

        // 쿼리 실행
        List<ReadRecipeInfoRes> readRecipeInfoResDTOList =
                recipeInfoRepository.findRecipeInfoPagingByCondition(builder,createOrderSpecifier(recipeSortType),pageable);

        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResDTOList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingRes.toDTO(readRecipeInfoResDTOList,recipeCnt);
    }

    public ReadRecipeInfoPagingRes readLikedRecipeInfoPaging(ReadRecipeInfoPagingReq readRecipeInfoPagingReq){

        String searchKeyword = readRecipeInfoPagingReq.getSearchKeyword();
        Long userId = readRecipeInfoPagingReq.getUserId();
        RecipeSortType recipeSortType = readRecipeInfoPagingReq.getRecipeSortType();
        Pageable pageable= PageRequest.of((int) (readRecipeInfoPagingReq.getPageNum()-1),10);

        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = recipeInfoRepository.findLikedRecipeInfoCnt(searchKeyword,userId);

        // 쿼리 실행
        List<ReadRecipeInfoRes> readRecipeInfoResDTOList =
                recipeInfoRepository.findLikedRecipeInfoList(createOrderSpecifier(recipeSortType),searchKeyword,pageable,userId);

        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResDTOList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingRes.toDTO(readRecipeInfoResDTOList, recipeCnt);
    }

    // 동적으로 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilder(Object condition, String searchKeyword, Long userId){
        BooleanBuilder builder = new BooleanBuilder();

        if(condition instanceof RecipeInfoType){
            // 레시피 타입으로 레시피 조회 시
            builder.and(recipeInfo.recipeInfoType.eq((RecipeInfoType) condition));
        }
        else if(condition instanceof String && condition.equals("mine")){
            // 자신이 쓴 레시피 조회 시
            builder.and(recipeInfo.user.userId.eq(userId));
        }

        if(searchKeyword != null){
            builder.and(recipeInfo.recipeName.contains(searchKeyword));
        }
        return builder;
    }

    // 정렬 조건을 동적으로 생성하는 메소드
    private OrderSpecifier[] createOrderSpecifier(RecipeSortType recipeSortType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(recipeSortType.equals(RecipeSortType.LATEST)){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, recipeInfo.regDate));
        }
        else if(recipeSortType.equals(RecipeSortType.CALOGY)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, recipeInfo.calogy));
        }
        else if(recipeSortType.equals(RecipeSortType.COOKTIME)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, recipeInfo.cookingTime));
        }
        else if(recipeSortType.equals(RecipeSortType.VISIT)){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, recipeInfo.visitCnt));
        }
        else{
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, recipeEmotion.count()));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
