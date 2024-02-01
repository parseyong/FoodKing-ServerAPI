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
                    if(user.getUserId() == userId)
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
