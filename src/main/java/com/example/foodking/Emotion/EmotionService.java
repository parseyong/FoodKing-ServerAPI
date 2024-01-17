package com.example.foodking.Emotion;

import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotion;
import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotionRepository;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotion;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotionRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeService;
import com.example.foodking.Reply.Reply;
import com.example.foodking.Reply.ReplyRepository;
import com.example.foodking.Reply.ReplyService;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import com.example.foodking.User.UserService;
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
    private final RecipeInfoRepository recipeInfoRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public void toggleReplyEmotion(Long userId, Long replyId,EmotionType emotionType){
        User user = UserService.findUserById(userId,userRepository);
        Reply reply = ReplyService.findReplyById(replyId,replyRepository);

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
            replyEmotionRepository.delete(result.get());
        }
    }

    @Transactional
    public void toggleRecipeInfoEmotion(Long userId, Long recipeInfoId, EmotionType emotionType){
        User user = UserService.findUserById(userId,userRepository);
        RecipeInfo recipeInfo = RecipeService.findRecipeInfoById(recipeInfoId,recipeInfoRepository);

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
            recipeEmotionRepository.delete(result.get());
        }
    }
}
