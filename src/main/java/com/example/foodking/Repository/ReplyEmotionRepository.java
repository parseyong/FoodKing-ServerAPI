package com.example.foodking.Repository;

import com.example.foodking.Domain.ReplyEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReplyEmotionRepository extends JpaRepository<ReplyEmotion,Long>,
        QuerydslPredicateExecutor<ReplyEmotion> {

}
