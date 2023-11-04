package com.example.foodking.Domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false)
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
