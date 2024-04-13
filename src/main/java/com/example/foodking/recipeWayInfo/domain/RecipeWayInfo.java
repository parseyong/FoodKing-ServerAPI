package com.example.foodking.recipeWayInfo.domain;

import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeWayInfo {

    @Id
    @Column(name = "recipe_way_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeWayInfoId;

    @Column(nullable = false,name = "recipe_way")
    private String recipeWay;

    @Column(nullable = false, name = "recipe_order")
    private Long recipeOrder;

    @ManyToOne
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    public RecipeWayInfo(String recipeWay, Long recipeOrder, RecipeInfo recipeInfo){
        this.recipeWay=recipeWay;
        this.recipeOrder=recipeOrder;
        this.recipeInfo=recipeInfo;
    }

    public void changeRecipeWay(String recipeWay){
        this.recipeWay=recipeWay;
    }

}
