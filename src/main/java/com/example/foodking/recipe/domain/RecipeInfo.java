package com.example.foodking.recipe.domain;

import com.example.foodking.common.TimeEntity;
import com.example.foodking.recipe.enums.RecipeInfoType;
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
@Table(indexes = {
        @Index(name = "like_index_recipe", columnList = "like_cnt, recipe_info_id"),
        @Index(name = "date_index_recipe", columnList = "reg_date, recipe_info_id"),
        @Index(name = "time_index_recipe", columnList = "cooking_time, recipe_info_id"),
        @Index(name = "calogy_index_recipe", columnList = "calogy, recipe_info_id")
})
public class RecipeInfo extends TimeEntity {

    @Id
    @Column(name = "recipe_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeInfoId;

    @Column(nullable = false, name = "recipe_name")
    private String recipeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "recipe_type")
    private RecipeInfoType recipeInfoType;

    @Column(nullable = false, name = "ingredient_cost")
    private Long ingredientCost;

    @Column(nullable = false, name = "cooking_time")
    private Long cookingTime;

    @Column(nullable = false)
    private Long calogy;

    @Column(name = "recipe_image")
    private String recipeImage;

    @Column(nullable = false, name = "recipe_tip")
    private String recipeTip;

    @Column(nullable = false, name = "visit_cnt")
    private Long visitCnt;

    @Column(nullable = false, name = "like_cnt")
    private Long likeCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Builder
    private RecipeInfo(String recipeName, RecipeInfoType recipeInfoType, Long ingredientCost,
                       Long cookingTime, Long calogy, String recipeTip, User user){
        this.recipeName=recipeName;
        this.recipeInfoType=recipeInfoType;
        this.ingredientCost=ingredientCost;
        this.cookingTime=cookingTime;
        this.calogy=calogy;
        this.recipeTip=recipeTip;
        this.user=user;
        this.visitCnt = 0L;
        this.likeCnt = 0L;
    }
    public void updateRecipeName(String recipeName){
        this.recipeName = recipeName;
    }
    public void updateRecipeInfoType(RecipeInfoType recipeInfoType){
        this.recipeInfoType = recipeInfoType;
    }
    public void updateIngredientCost(Long ingredientCost){
        this.ingredientCost = ingredientCost;
    }
    public void updateCookingTime(Long cookingTime){
        this.cookingTime = cookingTime;
    }
    public void updateCalogy(Long calogy){
        this.calogy = calogy;
    }
    public void addRecipeImage(String recipeImage){
        this.recipeImage=recipeImage;
    }
    public void deleteRecipeImage(){
        this.recipeImage=null;
    }
    public void updateRecipeTip(String recipeTip){
        this.recipeTip=recipeTip;
    }
    public void addVisitCnt(){
        this.visitCnt += 1;
    }
    public void liking(){
        this.likeCnt += 1;
    }
    public void unLiking(){
        if(likeCnt > 0 )
            this.likeCnt -=1;
    }
}
