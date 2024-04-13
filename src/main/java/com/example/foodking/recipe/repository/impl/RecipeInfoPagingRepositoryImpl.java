package com.example.foodking.recipe.repository.impl;

import com.example.foodking.recipe.domain.QRecipeInfo;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.recipe.repository.RecipeInfoPagingRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.foodking.emotion.domain.QRecipeEmotion.recipeEmotion;
import static com.example.foodking.recipe.domain.QRecipeInfo.recipeInfo;

@Repository
@RequiredArgsConstructor
public class RecipeInfoPagingRepositoryImpl implements RecipeInfoPagingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReadRecipeInfoResDTO> findRecipeInfoPagingByCondition
            (Pageable pageable, OrderSpecifier[] createOrderSpecifier, BooleanBuilder builder, Object condition) {

        // 만약 condition이 like면 쿼리문 구조가 달라지므로 따로 분리
        if(condition instanceof String && condition.equals("like")){
            return findLikeRecipeInfo(pageable,createOrderSpecifier,builder);
        }

        List<Tuple> result = jpaQueryFactory.select(recipeInfo,recipeEmotion.count())
                    .from(recipeInfo)
                    .leftJoin(recipeEmotion).on(recipeInfo.recipeInfoId.eq(recipeEmotion.recipeInfo.recipeInfoId))
                    .where(builder)
                    .groupBy(recipeInfo)
                    .orderBy(createOrderSpecifier)
                    .offset(pageable.getOffset()) // 시작지점
                    .limit(pageable.getPageSize()) //페이지의 크기
                    .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(QRecipeInfo.recipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(recipeEmotion.count());
                    return ReadRecipeInfoResDTO.toDTO(recipeInfo,replyCnt,emotionCnt);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long findRecipeInfoTotalCnt(BooleanBuilder builder, Object condition) {
        if(condition instanceof String && condition.equals("like"))
            return jpaQueryFactory.select(recipeInfo.count())
                    .from(recipeInfo)
                    .leftJoin(recipeEmotion).on(recipeInfo.recipeInfoId.eq(recipeEmotion.recipeInfo.recipeInfoId))
                    .where(builder)
                    .fetchOne();

        return jpaQueryFactory.select(recipeInfo.count())
                .from(recipeInfo)
                .where(builder)
                .fetchOne();
    }
    
    // 자신이 좋아요를 누른 레시피 조회
    private List<ReadRecipeInfoResDTO> findLikeRecipeInfo
            (Pageable pageable, OrderSpecifier[] createOrderSpecifier, BooleanBuilder builder){
        
        List<Tuple> result = jpaQueryFactory.select(recipeInfo,recipeEmotion.count())
                .from(recipeInfo)
                .join(recipeEmotion).on(recipeInfo.recipeInfoId.eq(recipeEmotion.recipeInfo.recipeInfoId))
                .where(builder)
                .groupBy(recipeInfo)
                .orderBy(createOrderSpecifier)
                .offset(pageable.getOffset()) // 시작지점
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(QRecipeInfo.recipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(recipeEmotion.count());
                    return ReadRecipeInfoResDTO.toDTO(recipeInfo,replyCnt,emotionCnt);
                })
                .collect(Collectors.toList());
    }
}
