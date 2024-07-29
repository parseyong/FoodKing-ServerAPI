package com.example.foodking.recipeWay.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipeWay is a Querydsl query type for RecipeWay
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipeWay extends EntityPathBase<RecipeWay> {

    private static final long serialVersionUID = -491742548L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeWay recipeWay1 = new QRecipeWay("recipeWay1");

    public final com.example.foodking.recipe.domain.QRecipeInfo recipeInfo;

    public final NumberPath<Long> recipeOrder = createNumber("recipeOrder", Long.class);

    public final StringPath recipeWay = createString("recipeWay");

    public final NumberPath<Long> recipeWayId = createNumber("recipeWayId", Long.class);

    public QRecipeWay(String variable) {
        this(RecipeWay.class, forVariable(variable), INITS);
    }

    public QRecipeWay(Path<? extends RecipeWay> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipeWay(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipeWay(PathMetadata metadata, PathInits inits) {
        this(RecipeWay.class, metadata, inits);
    }

    public QRecipeWay(Class<? extends RecipeWay> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipeInfo = inits.isInitialized("recipeInfo") ? new com.example.foodking.recipe.domain.QRecipeInfo(forProperty("recipeInfo"), inits.get("recipeInfo")) : null;
    }

}

