package com.example.foodking.emotion.repository;

import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.domain.ReplyEmotion;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReplyEmotionRepository extends CrudRepository<ReplyEmotion,Long> {

    Optional<ReplyEmotion> findByReplyAndUser(Reply reply, User user);

    Long countByReplyAndEmotionType(Reply reply, EmotionType emotionType);
}
