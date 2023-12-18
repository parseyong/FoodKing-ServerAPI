package com.example.foodking.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.Recipe.Recipe;
import com.example.foodking.RecipeInfo.RecipeInfo;
import com.example.foodking.RecipeInfo.RecipeInfoType;
import com.example.foodking.Reply.Reply;
import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipeInfo is a Querydsl query type for RecipeInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipeInfo extends EntityPathBase<RecipeInfo> {

    private static final long serialVersionUID = 1783131294L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeInfo recipeInfo = new QRecipeInfo("recipeInfo");

    public final QTimeEntity _super = new QTimeEntity(this);

    public final NumberPath<Long> calogy = createNumber("calogy", Long.class);

    public final StringPath cookingTime = createString("cookingTime");

    public final StringPath ingredientCost = createString("ingredientCost");

    public final ListPath<Ingredient, QIngredient> ingredientList = this.<Ingredient, QIngredient>createList("ingredientList", Ingredient.class, QIngredient.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath recipeImage = createString("recipeImage");

    public final NumberPath<Long> recipeInfoId = createNumber("recipeInfoId", Long.class);

    public final ListPath<Recipe, QRecipe> recipeList = this.<Recipe, QRecipe>createList("recipeList", Recipe.class, QRecipe.class, PathInits.DIRECT2);

    public final StringPath recipeName = createString("recipeName");

    public final StringPath recipeTip = createString("recipeTip");

    public final EnumPath<RecipeInfoType> recipeType = createEnum("recipeType", RecipeInfoType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final ListPath<Reply, QReply> replyList = this.<Reply, QReply>createList("replyList", Reply.class, QReply.class, PathInits.DIRECT2);

    public final QUser user;

    public QRecipeInfo(String variable) {
        this(RecipeInfo.class, forVariable(variable), INITS);
    }

    public QRecipeInfo(Path<? extends RecipeInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipeInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipeInfo(PathMetadata metadata, PathInits inits) {
        this(RecipeInfo.class, metadata, inits);
    }

    public QRecipeInfo(Class<? extends RecipeInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

