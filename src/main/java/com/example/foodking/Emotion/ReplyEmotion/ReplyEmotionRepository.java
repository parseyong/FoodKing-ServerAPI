package com.example.foodking.Emotion.ReplyEmotion;

import com.example.foodking.Reply.Reply;
import com.example.foodking.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ReplyEmotionRepository extends JpaRepository<ReplyEmotion,Long>,
        QuerydslPredicateExecutor<ReplyEmotion> {

    Optional<ReplyEmotion> findByReplyAndUser(Reply reply, User user);
}
