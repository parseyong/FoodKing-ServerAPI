package com.example.foodking.reply.domain;

import com.example.foodking.common.TimeEntity;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends TimeEntity {

    @Id
    @Column(name = "reply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, name = "like_cnt")
    private Long likeCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    private Reply(String content, User user, RecipeInfo recipeInfo){
        this.content=content;
        this.user=user;
        this.recipeInfo=recipeInfo;
        this.likeCnt = 0L;
    }

    public void changeContent(String content){
        this.content=content;
    }
    public void liking(){
        this.likeCnt += 1;
    }
    public void unLiking(){
        if(likeCnt > 0 )
            this.likeCnt -=1;
    }
}
