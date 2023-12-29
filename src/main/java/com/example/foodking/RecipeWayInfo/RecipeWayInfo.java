package com.example.foodking.RecipeWayInfo;

import com.example.foodking.RecipeInfo.RecipeInfo;
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
    public void RecipeWayInfo(String recipeWay, Long recipeOrder){
        this.recipeWay=recipeWay;
        this.recipeOrder=recipeOrder;
    }

    public void changeRecipeWay(String recipeWay){
        this.recipeWay=recipeWay;
    }

}
