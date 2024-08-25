package com.example.foodking.reply.repository;

import com.example.foodking.reply.dto.response.ReplyFindRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface ReplyPagingRepository {

    List<ReplyFindRes> findReplyPaging(BooleanBuilder builder, OrderSpecifier[] orderSpecifiers, Long userId);
}
