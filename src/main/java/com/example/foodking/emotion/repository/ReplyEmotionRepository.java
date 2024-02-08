package com.example.foodking.emotion.repository;

import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.domain.ReplyEmotion;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ReplyEmotionRepository extends JpaRepository<ReplyEmotion,Long>,
        QuerydslPredicateExecutor<ReplyEmotion> {

    Optional<ReplyEmotion> findByReplyAndUser(Reply reply, User user);

    Long countByReplyAndEmotionType(Reply reply, EmotionType emotionType);
}
