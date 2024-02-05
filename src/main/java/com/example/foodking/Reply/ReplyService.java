package com.example.foodking.Reply;

import com.example.foodking.Emotion.EmotionService;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Reply.DTO.Response.ReadReplyResDTO;
import com.example.foodking.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final EmotionService emotionService;
    @Transactional
    public Long addReply(User user, RecipeInfo recipeInfo, String content){
        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        return replyRepository.save(reply).getReplyId();
    }

    public List<ReadReplyResDTO> readReply(RecipeInfo recipeInfo,Long userId,ReplySortType replySortType){
        List<Reply> replyList = recipeInfo.getReplyList();
        List<ReadReplyResDTO> readReplyResDTOList = replyList.stream()
                .map(entity -> {
                    Long replyEmotionCnt = emotionService.readReplyEmotionCnt(entity);
                    User user = entity.getUser();
                    if(isMyReply(userId,user))
                        return ReadReplyResDTO.toDTO(entity, user.getNickName(), true,replyEmotionCnt);
                    else
                        return ReadReplyResDTO.toDTO(entity, user.getNickName(), false,replyEmotionCnt);
                })
                .sorted(getComparator(replySortType))
                .collect(Collectors.toList());
        return  readReplyResDTOList;
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;
        reply.changeContent(content);
        replyRepository.save(reply);
    }

    public Reply findReplyById(Long replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
    }

    public boolean isMyReply(Long userId, User user){
        if( user ==null || !userId.equals(user.getUserId()) )
            return false;

        return true;
    }

    private Comparator<ReadReplyResDTO> getComparator(ReplySortType replySortType) {
        switch (replySortType) {
            case LIKE:
                return Comparator.comparing(ReadReplyResDTO::getEmotionCnt).reversed();
            // 다른 정렬 기준에 따른 case 추가
            default:
                return Comparator.comparing(ReadReplyResDTO::getRegDate);
        }
    }

}
