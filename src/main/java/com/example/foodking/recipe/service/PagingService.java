package com.example.foodking.recipe.service;

import com.example.foodking.emotion.domain.QRecipeEmotion;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.domain.QRecipeInfo;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class PagingService {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    private final QRecipeInfo qRecipeInfo = QRecipeInfo.recipeInfo;
    private final QRecipeEmotion qRecipeEmotion = QRecipeEmotion.recipeEmotion;

    @Autowired
    public PagingService(EntityManager entityManager){
        this.entityManager=entityManager;
        jpaQueryFactory = new JPAQueryFactory(this.entityManager);
    }

    public List<ReadRecipeInfoResDTO> findPagingRecipeInfo
            (Pageable pageable,RecipeSortType recipeSortType,RecipeInfoType recipeInfoType){

        List<Tuple> result = jpaQueryFactory.select(qRecipeInfo,qRecipeEmotion.count())
                .from(qRecipeInfo)
                .leftJoin(qRecipeEmotion)
                .on(qRecipeInfo.recipeInfoId.eq(qRecipeEmotion.recipeInfo.recipeInfoId))
                .where(qRecipeInfo.recipeInfoType.eq(recipeInfoType))
                .groupBy(qRecipeInfo)
                .orderBy(createOrderSpecifier(recipeSortType))
                .offset(pageable.getOffset()) // 시작지점
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(qRecipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(qRecipeEmotion.count());
                    return ReadRecipeInfoResDTO.toDTO(recipeInfo,replyCnt,emotionCnt);
                })
                .collect(Collectors.toList());

    }
    // 총 레시피 수를 반환하는 메소드, fetchResult는 deprecated되었고 성능상 좋지 않기때문에 두 쿼리를 분리했다.
    public Long findRecipeInfoTotalCnt(RecipeInfoType recipeInfoType){

        return jpaQueryFactory.select(qRecipeInfo.count())
                .from(qRecipeInfo)
                .where(qRecipeInfo.recipeInfoType.eq(recipeInfoType))
                .fetchOne();
    }

    private OrderSpecifier[] createOrderSpecifier(RecipeSortType recipeSortType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(recipeSortType.equals(RecipeSortType.LATEST)){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, qRecipeInfo.regDate));
        }
        else if(recipeSortType.equals(RecipeSortType.CALOGY)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, qRecipeInfo.calogy));
        }
        else if(recipeSortType.equals(RecipeSortType.COOKTIME)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, qRecipeInfo.cookingTime));
        }
        else{
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, qRecipeEmotion.count()));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
