package com.example.foodking.reply.repository.impl;

import com.example.foodking.reply.dto.response.ReadReplyRes;
import com.example.foodking.reply.repository.ReplyQdslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.foodking.reply.domain.QReply.reply;
import static com.example.foodking.user.domain.QUser.user;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyQdslRepositoryImpl implements ReplyQdslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReadReplyRes> findReplyList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier, Long userId) {
        List<Tuple> result = jpaQueryFactory.select(reply, user.userId, user.nickName)
                .from(reply)
                .join(user).on(reply.user.userId.eq(user.userId))
                .where(builder)
                .orderBy(orderSpecifier)
                .limit(10)
                .fetch();

        return result.stream()
                .map(entity -> {
                    boolean isMyReply = userId == entity.get(user.userId);
                    return ReadReplyRes.toDTO(entity.get(reply),entity.get(user.nickName),isMyReply);
                })
                .collect(Collectors.toList());
    }

}
