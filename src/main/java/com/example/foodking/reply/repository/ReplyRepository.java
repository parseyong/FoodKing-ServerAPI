package com.example.foodking.reply.repository;

import com.example.foodking.reply.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReplyRepository extends JpaRepository<Reply,Long>,
        QuerydslPredicateExecutor<Reply> {

}
