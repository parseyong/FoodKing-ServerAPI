package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeId;

    @Column(nullable = false)
    private String recipeWay;

    @Column(nullable = false)
    private Long recipeOrder;

    @ManyToOne
    @JoinColumn(name = "recipe_info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    public void Recipe(String recipeWay, Long recipeOrder){
        this.recipeWay=recipeWay;
        this.recipeOrder=recipeOrder;
    }

    public void changeRecipeWay(String recipeWay){
        this.recipeWay=recipeWay;
    }

}
