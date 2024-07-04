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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = recipeInfoRepository.findRecipeInfoTotalCnt(getBuilderForCount(readRecipeInfoPagingReq));

        // 쿼리 실행
        List<ReadRecipeInfoRes> readRecipeInfoResList = recipeInfoRepository.findRecipeInfoPagingByCondition(
                getBuilderForPaging(readRecipeInfoPagingReq),
                createOrderSpecifier(readRecipeInfoPagingReq.getRecipeSortType()));

        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingRes.toDTO(readRecipeInfoResList,recipeCnt);
    }

    public ReadRecipeInfoPagingRes readLikedRecipeInfoPaging(ReadRecipeInfoPagingReq readRecipeInfoPagingReq){

        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = recipeInfoRepository.findLikedRecipeInfoCnt(getBuilderForCount(readRecipeInfoPagingReq));

        // 쿼리 실행
        List<ReadRecipeInfoRes> readRecipeInfoResDTOList = recipeInfoRepository.findLikedRecipeInfoList(
                getBuilderForPaging(readRecipeInfoPagingReq),
                createOrderSpecifier(readRecipeInfoPagingReq.getRecipeSortType()));

        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResDTOList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingRes.toDTO(readRecipeInfoResDTOList, recipeCnt);
    }

    // 동적으로 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilderForPaging(ReadRecipeInfoPagingReq readRecipeInfoPagingReq){
        BooleanBuilder builder = new BooleanBuilder();
        Object condition = readRecipeInfoPagingReq.getCondition();

        if(condition instanceof RecipeInfoType){
            // 레시피 타입으로 레시피 조회 시
            builder.and(recipeInfo.recipeInfoType.eq((RecipeInfoType) condition));
        }
        else if(condition instanceof String && condition.equals("mine")){
            // 자신이 쓴 레시피 조회 시
            builder.and(recipeInfo.user.userId.eq(readRecipeInfoPagingReq.getUserId()));
        }
        else if(condition instanceof String && condition.equals("like")){
            // 좋아요를 누른 레시피 조회 시
            builder.and(recipeEmotion.user.userId.eq(readRecipeInfoPagingReq.getUserId()));
        }

        if(readRecipeInfoPagingReq.getSearchKeyword() != null){
            builder.and(recipeInfo.recipeName.contains(readRecipeInfoPagingReq.getSearchKeyword()));
        }
        
        // lastId와 lastValue가 null이 아니라면 첫번째 페이지가 아니기 때문에 where절 추가
        if(readRecipeInfoPagingReq.getLastId() != null && readRecipeInfoPagingReq.getLastValue() != null){
            RecipeSortType recipeSortType = readRecipeInfoPagingReq.getRecipeSortType();

            if(recipeSortType.equals(RecipeSortType.LIKE)){
                builder.and(recipeInfo.likeCnt.loe(Long.valueOf(String.valueOf(readRecipeInfoPagingReq.getLastValue()))));
                builder.and(recipeInfo.recipeInfoId.gt(readRecipeInfoPagingReq.getLastId()));
            }
            else if(recipeSortType.equals(RecipeSortType.VISIT)){
                builder.and(recipeInfo.visitCnt.loe(Long.valueOf(String.valueOf(readRecipeInfoPagingReq.getLastValue()))));
                builder.and(recipeInfo.recipeInfoId.gt(readRecipeInfoPagingReq.getLastId()));
            }
            else if(recipeSortType.equals(RecipeSortType.CALOGY)){
                builder.and(recipeInfo.calogy.goe(Long.valueOf(String.valueOf(readRecipeInfoPagingReq.getLastValue()))));
                builder.and(recipeInfo.recipeInfoId.gt(readRecipeInfoPagingReq.getLastId()));
            }
            else if(recipeSortType.equals(RecipeSortType.COOKTIME)){
                builder.and(recipeInfo.cookingTime.goe(Long.valueOf(String.valueOf(readRecipeInfoPagingReq.getLastValue()))));
                builder.and(recipeInfo.recipeInfoId.gt(readRecipeInfoPagingReq.getLastId()));
            }
            else {
                builder.and(recipeInfo.regDate.loe(LocalDateTime.parse(
                        (String) readRecipeInfoPagingReq.getLastValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                builder.and(recipeInfo.recipeInfoId.gt(readRecipeInfoPagingReq.getLastId()));
            }

        }
        
        return builder;
    }

    private BooleanBuilder getBuilderForCount(ReadRecipeInfoPagingReq readRecipeInfoPagingReq){
        BooleanBuilder builder = new BooleanBuilder();
        Object condition = readRecipeInfoPagingReq.getCondition();

        if(condition instanceof RecipeInfoType){
            // 레시피 타입으로 레시피 조회 시
            builder.and(recipeInfo.recipeInfoType.eq((RecipeInfoType) condition));
        }
        else if(condition instanceof String && condition.equals("mine")){
            // 자신이 쓴 레시피 조회 시
            builder.and(recipeInfo.user.userId.eq(readRecipeInfoPagingReq.getUserId()));
        }
        else if(condition instanceof String && condition.equals("like")){
            // 좋아요를 누른 레시피 조회 시
            builder.and(recipeEmotion.user.userId.eq(readRecipeInfoPagingReq.getUserId()));
        }

        if(readRecipeInfoPagingReq.getSearchKeyword() != null){
            builder.and(recipeInfo.recipeName.contains(readRecipeInfoPagingReq.getSearchKeyword()));
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
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, recipeInfo.likeCnt));
        }

        orderSpecifiers.add(new OrderSpecifier(Order.ASC, recipeInfo.recipeInfoId));
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}