package com.example.foodking.ReplyEmotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReplyEmotionRepository extends JpaRepository<ReplyEmotion,Long>,
        QuerydslPredicateExecutor<ReplyEmotion> {

}
