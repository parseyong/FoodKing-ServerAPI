package com.example.foodking.Emotion;

import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotion;
import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotionRepository;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotion;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotionRepository;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Reply.Reply;
import com.example.foodking.User.User;
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

    @Transactional
    public void toggleReplyEmotion(User user, Reply reply,EmotionType emotionType){
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
    public void toggleRecipeInfoEmotion(User user, RecipeInfo recipeInfo, EmotionType emotionType){
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
