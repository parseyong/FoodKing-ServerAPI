package com.example.foodking.recipe.domain;

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

    private static final long serialVersionUID = -131304018L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeInfo recipeInfo = new QRecipeInfo("recipeInfo");

    public final com.example.foodking.common.QTimeEntity _super = new com.example.foodking.common.QTimeEntity(this);

    public final NumberPath<Long> calogy = createNumber("calogy", Long.class);

    public final NumberPath<Long> cookingTime = createNumber("cookingTime", Long.class);

    public final NumberPath<Long> ingredientCost = createNumber("ingredientCost", Long.class);

    public final ListPath<Ingredient, QIngredient> ingredientList = this.<Ingredient, QIngredient>createList("ingredientList", Ingredient.class, QIngredient.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath recipeImage = createString("recipeImage");

    public final NumberPath<Long> recipeInfoId = createNumber("recipeInfoId", Long.class);

    public final EnumPath<com.example.foodking.recipe.common.RecipeInfoType> recipeInfoType = createEnum("recipeInfoType", com.example.foodking.recipe.common.RecipeInfoType.class);

    public final StringPath recipeName = createString("recipeName");

    public final StringPath recipeTip = createString("recipeTip");

    public final ListPath<RecipeWayInfo, QRecipeWayInfo> recipeWayInfoList = this.<RecipeWayInfo, QRecipeWayInfo>createList("recipeWayInfoList", RecipeWayInfo.class, QRecipeWayInfo.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final ListPath<com.example.foodking.reply.domain.Reply, com.example.foodking.reply.domain.QReply> replyList = this.<com.example.foodking.reply.domain.Reply, com.example.foodking.reply.domain.QReply>createList("replyList", com.example.foodking.reply.domain.Reply.class, com.example.foodking.reply.domain.QReply.class, PathInits.DIRECT2);

    public final com.example.foodking.user.domain.QUser user;

    public final NumberPath<Long> visitCnt = createNumber("visitCnt", Long.class);

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
        this.user = inits.isInitialized("user") ? new com.example.foodking.user.domain.QUser(forProperty("user")) : null;
    }

}

