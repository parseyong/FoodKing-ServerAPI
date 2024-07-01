package com.example.foodking.reply.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.dto.response.ReadReplyRes;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.foodking.reply.domain.QReply.reply;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;

    @Transactional
    public Long addReply(Long userId, Long recipeInfoId, String content){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        return replyRepository.save(reply).getReplyId();
    }

    public List<ReadReplyRes> readReply(Long recipeId, Long userId,
                                        ReplySortType replySortType,
                                        Long lastId, Object lastValue,
                                        boolean isFirstPage){

        List<ReadReplyRes> replyResList = replyRepository.findReplyList(
                getBuilder(recipeId, replySortType, lastId, lastValue),
                createOrderSpecifier(replySortType),
                userId);

        if(replyResList.size() == 0 && !isFirstPage)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return replyResList;
    }

    @Transactional
    public void deleteReply(Long userId, Long replyId){

        Reply reply = findReplyById(replyId);

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;
        replyRepository.delete(reply);
    }

    @Transactional
    public void updateReply(Long userId, Long replyId, String content){

        Reply reply = findReplyById(replyId);

        if(!isMyReply(userId,reply.getUser()))
            throw new CommondException(ExceptionCode.ACCESS_FAIL_REPLY);;

        reply.changeContent(content);
        replyRepository.save(reply);
    }

    private Reply findReplyById(Long replyId){
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_REPLY));
    }

    private boolean isMyReply(Long userId, User user){
        if( user ==null || !userId.equals(user.getUserId()) )
            return false;

        return true;
    }

    // 동적으로 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilder(Long recipeId, ReplySortType replySortType, Long lastId, Object lastValue){
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(reply.recipeInfo.recipeInfoId.eq(recipeId));

        if(replySortType.equals(ReplySortType.LATEST) && lastId != null && lastValue != null){
            builder.and(reply.regDate.loe(LocalDateTime.parse((String)lastValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            builder.and(reply.replyId.gt(lastId));
        }
        else if(replySortType.equals(ReplySortType.LIKE) && lastId != null && lastValue != null){
            builder.and(reply.likeCnt.loe(Long.valueOf(String.valueOf(lastValue))));
            builder.and(reply.replyId.gt(lastId));
        }

        return builder;
    }

    // 정렬 조건을 동적으로 생성하는 메소드
    private OrderSpecifier[] createOrderSpecifier(ReplySortType replySortType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(replySortType.equals(ReplySortType.LATEST)){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, reply.regDate));
        }
        else{
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, reply.likeCnt));
        }

        orderSpecifiers.add(new OrderSpecifier(Order.ASC, reply.replyId));
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
