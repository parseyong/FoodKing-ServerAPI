package com.example.foodking.Repository;

import com.example.foodking.Domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReplyRepository extends JpaRepository<Reply,Long>,
        QuerydslPredicateExecutor<Reply> {

}
