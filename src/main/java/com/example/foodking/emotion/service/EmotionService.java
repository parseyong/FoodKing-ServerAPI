package com.example.foodking.emotion.service;

import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.domain.RecipeEmotion;
import com.example.foodking.emotion.domain.ReplyEmotion;
import com.example.foodking.emotion.repository.RecipeEmotionRepository;
import com.example.foodking.emotion.repository.ReplyEmotionRepository;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {

    private final ReplyEmotionRepository replyEmotionRepository;
    private final RecipeEmotionRepository recipeEmotionRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final RecipeInfoRepository recipeInfoRepository;

    @Transactional
    public void toggleReplyEmotion(Long userId, Long replyId, EmotionType emotionType){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        Reply  reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        Optional<ReplyEmotion> result = replyEmotionRepository.findByReplyAndUser(reply,user);
        if(result.isEmpty()){
            ReplyEmotion replyEmotion = ReplyEmotion.builder()
                    .emotionType(emotionType)
                    .user(user)
                    .reply(reply)
                    .build();
            replyEmotionRepository.save(replyEmotion);
        }
        else{
            ReplyEmotion replyEmotion = result.get();
            if(!isMyEmotion(user.getUserId(),replyEmotion.getUser()))
                throw new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION);

            replyEmotionRepository.delete(result.get());
        }
    }

    @Transactional
    public void toggleRecipeInfoEmotion(Long userId, Long recipeInfoId, EmotionType emotionType){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        RecipeInfo  recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        Optional<RecipeEmotion> result = recipeEmotionRepository.findByRecipeInfoAndUser(recipeInfo,user);
        if(result.isEmpty()){
            RecipeEmotion recipeEmotion = RecipeEmotion.builder()
                    .emotionType(emotionType)
                    .user(user)
                    .recipeInfo(recipeInfo)
                    .build();
            recipeEmotionRepository.save(recipeEmotion);
        }
        else{
            RecipeEmotion recipeEmotion = result.get();
            if(!isMyEmotion(user.getUserId(),recipeEmotion.getUser()))
                throw new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION);

            recipeEmotionRepository.delete(recipeEmotion);
        }
    }

    public Long readReplyEmotionCnt(Reply reply){
        return replyEmotionRepository.countByReplyAndEmotionType(reply,EmotionType.Like);
    }

    public Long readRecipeEmotionCnt(RecipeInfo recipeInfo){
        return recipeEmotionRepository.countByRecipeInfoAndEmotionType(recipeInfo,EmotionType.Like);
    }

    public boolean isMyEmotion(Long userId, User user){
        if( user ==null || !userId.equals(user.getUserId()) )
            return false;

        return true;
    }
}
