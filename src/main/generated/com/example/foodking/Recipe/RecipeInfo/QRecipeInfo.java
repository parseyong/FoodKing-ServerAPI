package com.example.foodking.Recipe.RecipeInfo;

import static com.querydsl.core.types.PathMetadataFactory.*;

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

    private static final long serialVersionUID = -223919082L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeInfo recipeInfo = new QRecipeInfo("recipeInfo");

    public final com.example.foodking.Common.QTimeEntity _super = new com.example.foodking.Common.QTimeEntity(this);

    public final NumberPath<Long> calogy = createNumber("calogy", Long.class);

    public final NumberPath<Long> cookingTime = createNumber("cookingTime", Long.class);

    public final NumberPath<Long> ingredientCost = createNumber("ingredientCost", Long.class);

    public final ListPath<com.example.foodking.Recipe.Ingredient.Ingredient, com.example.foodking.Recipe.Ingredient.QIngredient> ingredientList = this.<com.example.foodking.Recipe.Ingredient.Ingredient, com.example.foodking.Recipe.Ingredient.QIngredient>createList("ingredientList", com.example.foodking.Recipe.Ingredient.Ingredient.class, com.example.foodking.Recipe.Ingredient.QIngredient.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath recipeImage = createString("recipeImage");

    public final NumberPath<Long> recipeInfoId = createNumber("recipeInfoId", Long.class);

    public final EnumPath<RecipeInfoType> recipeInfoType = createEnum("recipeInfoType", RecipeInfoType.class);

    public final StringPath recipeName = createString("recipeName");

    public final StringPath recipeTip = createString("recipeTip");

    public final ListPath<com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo, com.example.foodking.Recipe.RecipeWayInfo.QRecipeWayInfo> recipeWayInfoList = this.<com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo, com.example.foodking.Recipe.RecipeWayInfo.QRecipeWayInfo>createList("recipeWayInfoList", com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo.class, com.example.foodking.Recipe.RecipeWayInfo.QRecipeWayInfo.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final ListPath<com.example.foodking.Reply.Reply, com.example.foodking.Reply.QReply> replyList = this.<com.example.foodking.Reply.Reply, com.example.foodking.Reply.QReply>createList("replyList", com.example.foodking.Reply.Reply.class, com.example.foodking.Reply.QReply.class, PathInits.DIRECT2);

    public final com.example.foodking.User.QUser user;

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
        this.user = inits.isInitialized("user") ? new com.example.foodking.User.QUser(forProperty("user")) : null;
    }

}

