package com.example.foodking.recipe.repository.impl;

import com.example.foodking.recipe.domain.QRecipeInfo;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoRes;
import com.example.foodking.recipe.repository.RecipeInfoQdslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.foodking.emotion.domain.QRecipeEmotion.recipeEmotion;
import static com.example.foodking.recipe.domain.QRecipeInfo.recipeInfo;
import static com.example.foodking.reply.domain.QReply.reply;
import static com.example.foodking.user.domain.QUser.user;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeInfoQdslRepositoryImpl implements RecipeInfoQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReadRecipeInfoRes> findRecipeInfoPagingByCondition(BooleanBuilder builder, OrderSpecifier[] orderSpecifier, Pageable pageable) {

        List<Tuple> result = jpaQueryFactory.select(recipeInfo,user.nickName,user.userId,recipeEmotion.count())
                    .from(recipeInfo)
                    .leftJoin(recipeEmotion).on(recipeInfo.recipeInfoId.eq(recipeEmotion.recipeInfo.recipeInfoId))
                    .join(user).on(recipeInfo.user.userId.eq(user.userId))
                    .where(builder)
                    .groupBy(recipeInfo)
                    .orderBy(orderSpecifier)
                    .offset(pageable.getOffset()) // 시작지점
                    .limit(pageable.getPageSize()) //페이지의 크기
                    .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(QRecipeInfo.recipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(recipeEmotion.count());
                    Long writerUserId = entity.get(user.userId);
                    String writerNickName = entity.get(user.nickName);
                    return ReadRecipeInfoRes.toDTO(recipeInfo,replyCnt,writerUserId,writerNickName);
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
    public List<ReadRecipeInfoRes> findLikedRecipeInfoList(OrderSpecifier[] orderSpecifier, String searchKeyword, Pageable pageable, Long userId) {
        BooleanBuilder builder = new BooleanBuilder();

        if(searchKeyword != null)
            builder.and(recipeInfo.recipeName.contains(searchKeyword));

        builder.and(recipeEmotion.user.userId.eq(userId));

        List<Long> likedRecipeInfoIdList = jpaQueryFactory.select(recipeInfo.recipeInfoId).distinct()
                .from(recipeEmotion)
                .join(recipeInfo).on(recipeEmotion.recipeInfo.recipeInfoId.eq(recipeInfo.recipeInfoId))
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset()) // 시작지점
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        List<Tuple> result = jpaQueryFactory.select(recipeInfo,user.userId,user.nickName,recipeEmotion.count())
                .from(recipeInfo)
                .join(recipeEmotion).on(recipeInfo.recipeInfoId.eq(recipeEmotion.recipeInfo.recipeInfoId))
                .join(user).on(recipeInfo.user.userId.eq(user.userId))
                .where(recipeInfo.recipeInfoId.in(likedRecipeInfoIdList))
                .groupBy(recipeInfo)
                .orderBy(orderSpecifier)
                .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(QRecipeInfo.recipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(recipeEmotion.count());
                    Long writerUserId = entity.get(user.userId);
                    String writerNickName = entity.get(user.nickName);
                    return ReadRecipeInfoRes.toDTO(recipeInfo,replyCnt,writerUserId,writerNickName);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long findLikedRecipeInfoCnt(String searchKeyword, Long userId) {
        BooleanBuilder builder = new BooleanBuilder();

        if(searchKeyword != null)
            builder.and(recipeInfo.recipeName.contains(searchKeyword));

        builder.and(recipeEmotion.user.userId.eq(userId));

        return jpaQueryFactory.select(recipeInfo.recipeInfoId.countDistinct())
                .from(recipeEmotion)
                .join(recipeInfo).on(recipeEmotion.recipeInfo.recipeInfoId.eq(recipeInfo.recipeInfoId))
                .where(builder)
                .fetchOne();
    }

    @Override
    public ReadRecipeInfoRes findRecipeInfo(Long recipeinfoId) {
        Tuple result = jpaQueryFactory.select(recipeInfo,user.nickName,user.userId)
                .from(recipeInfo)
                .join(user).on(recipeInfo.user.userId.eq(user.userId))
                .where(recipeInfo.recipeInfoId.eq(recipeinfoId))
                .fetchOne();

        Long replyCnt = jpaQueryFactory.select(reply.count())
                .from(reply)
                .where(reply.recipeInfo.recipeInfoId.eq(recipeinfoId))
                .fetchOne();

        return ReadRecipeInfoRes.toDTO
                (result.get(recipeInfo),replyCnt,result.get(user.userId),result.get(user.nickName));
    }

}
