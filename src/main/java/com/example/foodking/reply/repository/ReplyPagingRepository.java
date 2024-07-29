package com.example.foodking.reply.repository;

import com.example.foodking.reply.dto.response.ReplyFindRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface ReplyPagingRepository {

    List<ReplyFindRes> findReplyList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier, Long userId);
}
