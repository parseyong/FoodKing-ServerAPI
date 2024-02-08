package com.example.foodking.recipe.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipeWayInfo is a Querydsl query type for RecipeWayInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipeWayInfo extends EntityPathBase<RecipeWayInfo> {

    private static final long serialVersionUID = 221700925L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeWayInfo recipeWayInfo = new QRecipeWayInfo("recipeWayInfo");

    public final QRecipeInfo recipeInfo;

    public final NumberPath<Long> recipeOrder = createNumber("recipeOrder", Long.class);

    public final StringPath recipeWay = createString("recipeWay");

    public final NumberPath<Long> recipeWayInfoId = createNumber("recipeWayInfoId", Long.class);

    public QRecipeWayInfo(String variable) {
        this(RecipeWayInfo.class, forVariable(variable), INITS);
    }

    public QRecipeWayInfo(Path<? extends RecipeWayInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipeWayInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipeWayInfo(PathMetadata metadata, PathInits inits) {
        this(RecipeWayInfo.class, metadata, inits);
    }

    public QRecipeWayInfo(Class<? extends RecipeWayInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipeInfo = inits.isInitialized("recipeInfo") ? new QRecipeInfo(forProperty("recipeInfo"), inits.get("recipeInfo")) : null;
    }

}

