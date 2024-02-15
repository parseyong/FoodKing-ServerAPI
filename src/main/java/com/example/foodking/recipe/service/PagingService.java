package com.example.foodking.recipe.service;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.emotion.domain.QRecipeEmotion;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.common.RecipeInfoType;
import com.example.foodking.recipe.common.RecipeSortType;
import com.example.foodking.recipe.domain.QRecipeInfo;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.dto.recipeInfo.request.ReadRecipeInfoPagingReqDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoPagingResDTO;
import com.example.foodking.recipe.dto.recipeInfo.response.ReadRecipeInfoResDTO;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class PagingService {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    private final QRecipeInfo qRecipeInfo = QRecipeInfo.recipeInfo;
    private final QRecipeEmotion qRecipeEmotion = QRecipeEmotion.recipeEmotion;
    private final UserRepository userRepository;

    @Autowired
    public PagingService(EntityManager entityManager,UserRepository userRepository){
        this.entityManager=entityManager;
        this.userRepository=userRepository;
        jpaQueryFactory = new JPAQueryFactory(this.entityManager);
    }
    
    /*
        컨트롤러로부터 request처리를 위임받고 처리결과를 반환하는 메소드로 페이징기능에 대한 main메소드? 의 역할
        자신이 쓴 레시피, 졸아요를 누른 레시피, 레시피 타입에 대한 검색, 키워드 검색에 대한 요청을 모두 해당 메소드가 처리한다.
    */
    public ReadRecipeInfoPagingResDTO readRecipeInfoPagingByCondition(Long pageNum, ReadRecipeInfoPagingReqDTO readRecipeInfoPagingReqDTO, Object condition){
        RecipeSortType recipeSortType = readRecipeInfoPagingReqDTO.getRecipeSortType();
        String searchKeyword = readRecipeInfoPagingReqDTO.getSearchKeyword();

        // Pageable 객체 생성, 한 페이지에 10개의 레시피를 보여주도록 구현
        Pageable pageable= PageRequest.of((int) (pageNum-1),10);
        
        // 조건(condition)에 따라 동적으로 WHERE절을 생성
        BooleanBuilder builder = getBuilder(condition,searchKeyword);
        
        // 해당 조건에 대한 전체 결과 수 측정
        Long recipeCnt = findRecipeInfoTotalCnt(builder,condition);
        
        // 쿼리 실행
        List<ReadRecipeInfoResDTO> readRecipeInfoResDTOList = findRecipeInfoPaging(pageable ,recipeSortType, builder);
        
        // 존재하지 않는 페이지일 경우 예외를 던짐
        if(readRecipeInfoResDTOList.size() == 0)
            throw new CommondException(ExceptionCode.NOT_EXIST_PAGE);

        return ReadRecipeInfoPagingResDTO.toDTO(readRecipeInfoResDTOList,recipeCnt);
    }
    
    // 동적으로 생성된 WHERE절과 정렬조건에 따라 쿼리를 실행하는 메소드, DTO로 래핑하여 반환한다.
    private List<ReadRecipeInfoResDTO> findRecipeInfoPaging(Pageable pageable, RecipeSortType recipeSortType, BooleanBuilder builder){

        List<Tuple> result = jpaQueryFactory.select(qRecipeInfo,qRecipeEmotion.count())
                .from(qRecipeInfo)
                .leftJoin(qRecipeEmotion)
                .on(qRecipeInfo.recipeInfoId.eq(qRecipeEmotion.recipeInfo.recipeInfoId))
                .where(builder)
                .groupBy(qRecipeInfo)
                .orderBy(createOrderSpecifier(recipeSortType))
                .offset(pageable.getOffset()) // 시작지점
                .limit(pageable.getPageSize()) //페이지의 크기
                .fetch();

        return result.stream()
                .map(entity -> {
                    RecipeInfo recipeInfo = entity.get(qRecipeInfo);
                    Long replyCnt = (long) recipeInfo.getReplyList().size();
                    Long emotionCnt = entity.get(qRecipeEmotion.count());
                    return ReadRecipeInfoResDTO.toDTO(recipeInfo,replyCnt,emotionCnt);
                })
                .collect(Collectors.toList());

    }

    // 총 레시피 수를 반환하는 메소드, fetchResult는 deprecated되었고 성능상 좋지 않기때문에 두 쿼리를 분리했다.
    private Long findRecipeInfoTotalCnt(BooleanBuilder builder,Object condition){

        if(condition instanceof String && condition.equals("like"))
            return jpaQueryFactory.select(qRecipeInfo.count())
                    .from(qRecipeInfo)
                    .leftJoin(qRecipeEmotion)
                    .on(qRecipeInfo.recipeInfoId.eq(qRecipeEmotion.recipeInfo.recipeInfoId))
                    .where(builder)
                    .fetchOne();

        return jpaQueryFactory.select(qRecipeInfo.count())
                .from(qRecipeInfo)
                .where(builder)
                .fetchOne();
    }
    
    // 정렬 조건을 동적으로 생성하는 메소드
    private OrderSpecifier[] createOrderSpecifier(RecipeSortType recipeSortType) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(recipeSortType.equals(RecipeSortType.LATEST)){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, qRecipeInfo.regDate));
        }
        else if(recipeSortType.equals(RecipeSortType.CALOGY)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, qRecipeInfo.calogy));
        }
        else if(recipeSortType.equals(RecipeSortType.COOKTIME)){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, qRecipeInfo.cookingTime));
        }
        else{
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, qRecipeEmotion.count()));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    // 동적으로 쿼리의 WHERE절을 생성하는 메소드
    private BooleanBuilder getBuilder(Object condition, String searchKeyword){
        BooleanBuilder builder = new BooleanBuilder();

        if(condition instanceof RecipeInfoType ){
            // 레시피 타입으로 레시피 조회 시
            builder.and(qRecipeInfo.recipeInfoType.eq((RecipeInfoType) condition));
        }
        else if(condition instanceof String && condition.equals("mine")){
            // 자신이 쓴 레시피 조회 시
            Long userId = JwtProvider.getUserId();
            User user = findByUserId(userId);
            builder.and(qRecipeInfo.user.eq(findByUserId(userId)));
        }
        else if(condition instanceof String && condition.equals("like")){
            // 좋아요 누른 레시피 조회 시
            Long userId = JwtProvider.getUserId();
            User user = findByUserId(userId);
            builder.and(qRecipeEmotion.user.eq(user));
        }

        // 위 3가지 조회방법에 키워드 검색을 같이 사용할 경우 or 단순 키워드검색을 사용할 경우
        if(searchKeyword != null){
            builder.and(qRecipeInfo.recipeName.contains(searchKeyword));
        }
        return builder;
    }

    private User findByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }
}
