package com.example.foodking.recipe.repository.impl;

import com.example.foodking.recipe.domain.QRecipeInfo;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.response.RecipeInfoFindRes;
import com.example.foodking.recipe.repository.RecipeInfoPaingRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.foodking.emotion.domain.QRecipeEmotion.recipeEmotion;
import static com.example.foodking.recipe.domain.QRecipeInfo.recipeInfo;
import static com.example.foodking.user.domain.QUser.user;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeInfoPaingRepositoryImpl implements RecipeInfoPaingRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<RecipeInfoFindRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier) {

        List<Tuple> tupleList = jpaQueryFactory.select(recipeInfo,user.nickName,user.userId)
                .from(recipeInfo)
                .join(user).on(recipeInfo.user.userId.eq(user.userId))
                .where(builder)
                .orderBy(orderSpecifier)
                .limit(10) //페이지의 크기
                .fetch();

        return tupleList.stream()
                .map(tuple -> {
                    RecipeInfo recipeInfo = tuple.get(QRecipeInfo.recipeInfo);
                    Long writerUserId = tuple.get(user.userId);
                    String writerNickName = tuple.get(user.nickName);
                    return RecipeInfoFindRes.toDTO(recipeInfo,writerUserId,writerNickName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long findRecipeInfoTotalCnt(BooleanBuilder builder) {
        return jpaQueryFactory.select(recipeInfo.count())
                .from(recipeInfo)
                .where(builder)
                .fetchOne();
    }

    @Override
    public List<RecipeInfoFindRes> findLikedRecipeInfoList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier) {

        /*
            좋아요를 누른 레시피를 가져오는 쿼리에서는 두개의 쿼리로 분리하여 진행했습니다.

            1. 먼저 좋아요를 누른 레시피의 id만 가져오는 쿼리
            2. 레시피id에 해당하는 정보를 가져오는 쿼리

            하나의 쿼리로 모든 정보를 가져오는 방향으로 쿼리튜닝을 진행했지만 복잡한 join조건과
            여러개의 일대다 관계를 가지는 테이블간 조인이 여러개 이루어지다보니 조인테이블의 양이 증가함에따라
            성능이 오히려 저하되는 모습이 확인되어 두 개의 쿼리로 분리하여 로직을 수행하도록 했습니다.
        */

        List<Long> likedRecipeInfoIdList = jpaQueryFactory.select(recipeInfo.recipeInfoId).distinct()
                .from(recipeEmotion)
                .join(recipeInfo).on(recipeEmotion.recipeInfo.recipeInfoId.eq(recipeInfo.recipeInfoId))
                .where(builder)
                .orderBy(orderSpecifier)
                .limit(10) //페이지의 크기
                .fetch();

        List<Tuple> tupleList = jpaQueryFactory.select(recipeInfo,user.userId,user.nickName)
                .from(recipeInfo)
                .join(user).on(recipeInfo.user.userId.eq(user.userId))
                .where(recipeInfo.recipeInfoId.in(likedRecipeInfoIdList))
                .orderBy(orderSpecifier)
                .limit(10) //페이지의 크기
                .fetch();

        return tupleList.stream()
                .map(tuple -> {
                    RecipeInfo recipeInfo = tuple.get(QRecipeInfo.recipeInfo);
                    Long writerUserId = tuple.get(user.userId);
                    String writerNickName = tuple.get(user.nickName);
                    return RecipeInfoFindRes.toDTO(recipeInfo,writerUserId,writerNickName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long findLikedRecipeInfoCnt(BooleanBuilder builder) {
        // 한사람당 레시피에 하나의 좋아요만 누를 수 있지만 더미데이터를 추가할 때
        // 편의상 하나의 유저가 동일레시피에 좋아요를 여러번 눌러진 형태로 집어넣었기 때문에 countDistinct()를 추가했습니다.
        return jpaQueryFactory.select(recipeInfo.recipeInfoId.countDistinct())
                .from(recipeEmotion)
                .join(recipeInfo).on(recipeEmotion.recipeInfo.recipeInfoId.eq(recipeInfo.recipeInfoId))
                .where(builder)
                .fetchOne();
    }

    @Override
    public RecipeInfoFindRes findRecipeInfo(Long recipeinfoId) {
        Tuple tuple = jpaQueryFactory.select(recipeInfo,user.nickName,user.userId)
                .from(recipeInfo)
                .join(user).on(recipeInfo.user.userId.eq(user.userId))
                .where(recipeInfo.recipeInfoId.eq(recipeinfoId))
                .fetchOne();

        return RecipeInfoFindRes.toDTO
                (tuple.get(recipeInfo),tuple.get(user.userId),tuple.get(user.nickName));
    }

}
