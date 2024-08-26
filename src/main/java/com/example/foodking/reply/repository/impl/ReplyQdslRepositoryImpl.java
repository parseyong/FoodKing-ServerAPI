package com.example.foodking.reply.repository.impl;

import com.example.foodking.reply.dto.response.ReplyFindRes;
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
    public List<ReplyFindRes> findReplyPaging(BooleanBuilder builder, OrderSpecifier[] orderSpecifiers, Long userId) {
        List<Tuple> tuples = jpaQueryFactory.select(reply, user.userId, user.nickName)
                .from(reply)
                .join(user).on(reply.user.userId.eq(user.userId))
                .where(builder)
                .orderBy(orderSpecifiers)
                .limit(10)
                .fetch();

        return tuples.stream()
                .map(tuple -> {
                    boolean isMyReply = userId == tuple.get(user.userId);
                    return ReplyFindRes.toDTO(tuple.get(reply), tuple.get(user.nickName), isMyReply);
                })
                .collect(Collectors.toList());
    }

}
