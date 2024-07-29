package com.example.foodking.emotion.service;

import com.example.foodking.aop.distributedLock.DistributedLock;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final ReplyEmotionRepository replyEmotionRepository;
    private final RecipeEmotionRepository recipeEmotionRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final RecipeInfoRepository recipeInfoRepository;


    @DistributedLock(key = "#LockReplyEmotion")
    public void toggleReplyEmotion(Long userId, Long replyId, EmotionType emotionType){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        Reply  reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        Optional<ReplyEmotion> replyEmotionOptional = replyEmotionRepository.findByReplyAndUser(reply,user);
        
        // 만약 등록된 이모션이 없다면 이모션 등록
        if(replyEmotionOptional.isEmpty()){
            ReplyEmotion replyEmotion = ReplyEmotion.builder()
                    .emotionType(emotionType)
                    .user(user)
                    .reply(reply)
                    .build();
            replyEmotionRepository.save(replyEmotion);
            reply.liking();
        }
        // 만약 등록된 이모션이 있다면 해당 이모션 삭제
        else{
            ReplyEmotion replyEmotion = replyEmotionOptional.get();
            
            // 이모션 삭제권한이 없으면 예외반환
            if(!isMyEmotion(user.getUserId(),replyEmotion.getUser()))
                throw new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION);

            replyEmotionRepository.delete(replyEmotionOptional.get());
            reply.unLiking();
        }
        replyRepository.save(reply);
    }

    @DistributedLock(key = "#LockRecipeEmotion")
    public void toggleRecipeInfoEmotion(Long userId, Long recipeInfoId, EmotionType emotionType){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        RecipeInfo  recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        Optional<RecipeEmotion> recipeEmotionOptional = recipeEmotionRepository.findByRecipeInfoAndUser(recipeInfo,user);

        // 만약 등록된 이모션이 없다면 이모션 등록
        if(recipeEmotionOptional.isEmpty()){
            RecipeEmotion recipeEmotion = RecipeEmotion.builder()
                    .emotionType(emotionType)
                    .user(user)
                    .recipeInfo(recipeInfo)
                    .build();
            recipeEmotionRepository.save(recipeEmotion);
            recipeInfo.liking();
        }
        // 만약 등록된 이모션이 있다면 해당 이모션 삭제
        else{
            RecipeEmotion recipeEmotion = recipeEmotionOptional.get();
            // 이모션 삭제권한이 없으면 예외반환
            if(!isMyEmotion(user.getUserId(),recipeEmotion.getUser()))
                throw new CommondException(ExceptionCode.ACCESS_FAIL_EMOTION);

            recipeEmotionRepository.delete(recipeEmotion);
            recipeInfo.unLiking();
        }
        recipeInfoRepository.save(recipeInfo);
    }

    private boolean isMyEmotion(Long userId, User user){
        if( user ==null || !userId.equals(user.getUserId()) )
            return false;

        return true;
    }

}
