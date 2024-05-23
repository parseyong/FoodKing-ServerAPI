package com.example.foodking.reply.repository;

import com.example.foodking.reply.dto.response.ReadReplyRes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

public interface ReplyQdslRepository {

    List<ReadReplyRes> findReplyList(BooleanBuilder builder, OrderSpecifier[] orderSpecifier, Long userId);
}
