package com.example.foodking.recipe.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.dto.recipeInfo.request.ReadRecipeInfoPagingReqDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoPagingResDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
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
public class PagingService {

    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;

    public ReadRecipeInfoPagingResDTO readRecipeInfoPagingByCondition(Long userId, Long pageNum, ReadRecipeInfoPagingReqDTO readRecipeInfoPagingReqDTO, Object condition){
        RecipeSortType recipeSortType = readRecipeInfoPagingReqDTO.getRecipeSortType();
        String searchKeyword = readRecipeInfoPagingReqDTO.getSearchKeyword();

        Pageable pageable= PageRequest.of((int) (pageNum-1),10);

        // 조건(condition)에 따라 동적으로 WHERE절을 생성
        BooleanBuilder builder = getBuilder(condition,searchKeyword,userId);

        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = recipeInfoRepository.findRecipeInfoTotalCnt(builder,condition);

        // 쿼리 실행
        List<ReadRecipeInfoResDTO> readRecipeInfoResDTOList =
                recipeInfoRepository.findRecipeInfoPaging(pageable ,createOrderSpecifier(recipeSortType), builder);

        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResDTOList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingResDTO.toDTO(readRecipeInfoResDTOList,recipeCnt);
    }

    // 동적으로 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilder(Object condition, String searchKeyword, Long userId){
        BooleanBuilder builder = new BooleanBuilder();

        if(condition instanceof RecipeInfoType ){
            // 레시피 타입으로 레시피 조회 시
            builder.and(recipeInfo.recipeInfoType.eq((RecipeInfoType) condition));
        }
        else if(condition instanceof String && condition.equals("mine")){
            // 자신이 쓴 레시피 조회 시
            User user = findByUserId(userId);
            builder.and(recipeInfo.user.eq(findByUserId(userId)));
        }
        else if(condition instanceof String && condition.equals("like")){
            // 좋아요 누른 레시피 조회 시
            User user = findByUserId(userId);
            builder.and(recipeEmotion.user.eq(user));
        }

        // 위 3가지 조회방법에 키워드 검색을 같이 사용할 경우 or 단순 키워드검색을 사용할 경우
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

    private User findByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }
}
