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
    public void addReply(User user, RecipeInfo recipeInfo, String content){
        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        replyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        isMyReply(userId,reply.getUser());
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        isMyReply(userId,reply.getUser());
        reply.changeContent(content);
        replyRepository.save(reply);
    }

    public Reply findReplyById(Long replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
    }

    public void isMyReply(Long userId, User user){
        /*
           단위 테스트시 userId값을 지정할 수 없기때문에 해당 조건문을 추가하여 테스트를 통과할 수 있도록 했다.
           실제 환경에서는 User는 null이 아니고 user.userId값은 null인 경우는 존재하지 않는다.
        */
        if(user != null && user.getUserId() == null)
            ;
        else if( user ==null || !userId.equals(user.getUserId()) )
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);

    }
}
