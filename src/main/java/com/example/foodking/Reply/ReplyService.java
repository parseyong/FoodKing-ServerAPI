package com.example.foodking.Reply;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeService;
import com.example.foodking.User.User;
import com.example.foodking.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserService userService;
    private final RecipeService recipeService;

    @Transactional
    public Long addReply(Long userId, Long recipeInfoId, String content){
        User user = userService.findUserById(userId);
        RecipeInfo recipeInfo = recipeService.findRecipeInfoById(recipeInfoId);

        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        Reply savedReply = replyRepository.save(reply);
        return savedReply.getReplyId();
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId){
        Reply reply = findReplyById(replyId);
        isMyReply(reply,userId);
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){
        Reply reply = findReplyById(replyId);
        isMyReply(reply,userId);
        reply.changeContent(content);
        replyRepository.save(reply);
    }

    public Reply findReplyById(Long replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
    }

    public void isMyReply(Reply reply, Long userId){
        if(reply.getUser().getUserId() != userId )
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);
    }
}
