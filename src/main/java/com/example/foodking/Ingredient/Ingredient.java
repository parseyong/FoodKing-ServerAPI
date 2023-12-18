package com.example.foodking.Ingredient;

import com.example.foodking.RecipeInfo.RecipeInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {

    @Id
    @Column(name = "ingredient_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false, name = "ingredient_name")
    private String ingredientName;

    @ManyToOne
    @JoinColumn(name = "recipe_Info_id",nullable = false)
    private RecipeInfo recipeInfo;

    @Builder
    public Ingredient(String ingredientName){
        this.ingredientName=ingredientName;
    }
    public void changeIngredientName(String ingredientName){
        this.ingredientName=ingredientName;
    }

}
