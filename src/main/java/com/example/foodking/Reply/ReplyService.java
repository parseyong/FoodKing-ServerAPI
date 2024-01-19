package com.example.foodking.Reply;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public Long addReply(User user, RecipeInfo recipeInfo, String content){
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
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
        isMyReply(reply,userId);
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
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
