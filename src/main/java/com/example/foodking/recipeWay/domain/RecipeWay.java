package com.example.foodking.recipeWay.domain;

import com.example.foodking.recipe.domain.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeWay {

    @Id
    @Column(name = "recipe_way_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeWayId;

    @Column(nullable = false,name = "recipe_way")
    private String recipeWay;

    @Column(nullable = false, name = "recipe_order")
    private Long recipeOrder;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    private RecipeWay(String recipeWay, Long recipeOrder, RecipeInfo recipeInfo){
        this.recipeWay=recipeWay;
        this.recipeOrder=recipeOrder;
        this.recipeInfo=recipeInfo;
    }

    public void updateRecipeWay(String recipeWay){
        this.recipeWay=recipeWay;
    }

}
