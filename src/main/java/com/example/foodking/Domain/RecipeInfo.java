package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeInfo extends  TimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeInfoId;

    @Column(nullable = false)
    private String recipeName;

    @Column(nullable = false)
    private RecipeType recipeType;

    @Column(nullable = false)
    private String ingredientCost;

    @Column(nullable = false)
    private String cookingTime;

    @Column(nullable = false)
    private Long calogy;

    @Column
    private String recipeImage;

    @Column(nullable = false)
    private String recipeTip;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Reply> replyList;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recipe> recipeList;

    @OneToMany(mappedBy = "recipeInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ingredient> ingredientList;

    @Builder
    public RecipeInfo(String recipeName, RecipeType recipeType, String ingredientCost, String cookingTime, Long calogy, String recipeTip,
                      User user, List<Recipe> recipeList, List<Ingredient> ingredientList){
        this.recipeName=recipeName;
        this.recipeType=recipeType;
        this.ingredientCost=ingredientCost;
        this.cookingTime=cookingTime;
        this.calogy=calogy;
        this.recipeTip=recipeTip;
        this.user=user;
        this.recipeList=recipeList;
        this.ingredientList=ingredientList;
    }
    public void changeRecipeName(String recipeName){
        this.recipeName = recipeName;
    }
    public void changeRecipeType(RecipeType recipeType){
        this.recipeType = recipeType;
    }
    public void changeIngredientCost(String ingredientCost){
        this.ingredientCost = ingredientCost;
    }
    public void changeCookingTime(String cookingTime){
        this.cookingTime = cookingTime;
    }
    public void changeCalogy(Long calogy){
        this.calogy = calogy;
    }
    public void addRecipeImage(String recipeImage){
        this.recipeImage=recipeImage;
    }
    public void changeRecipeTip(String recipeTip){
        this.recipeTip=recipeTip;
    }
    public void changeRecipeList(List<Recipe> recipeList){
        this.recipeList=recipeList;
    }
    public void changeIngredientList(List<Ingredient> ingredientList){
        this.ingredientList=ingredientList;
    }
}
