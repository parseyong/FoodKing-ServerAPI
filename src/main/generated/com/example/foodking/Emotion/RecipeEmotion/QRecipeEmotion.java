package com.example.foodking.Emotion.RecipeEmotion;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecipeEmotion is a Querydsl query type for RecipeEmotion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipeEmotion extends EntityPathBase<RecipeEmotion> {

    private static final long serialVersionUID = -353657327L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecipeEmotion recipeEmotion = new QRecipeEmotion("recipeEmotion");

    public final EnumPath<com.example.foodking.Emotion.EmotionType> emotionType = createEnum("emotionType", com.example.foodking.Emotion.EmotionType.class);

    public final NumberPath<Long> recipeEmotionId = createNumber("recipeEmotionId", Long.class);

    public final com.example.foodking.Recipe.RecipeInfo.QRecipeInfo recipeInfo;

    public final com.example.foodking.User.QUser user;

    public QRecipeEmotion(String variable) {
        this(RecipeEmotion.class, forVariable(variable), INITS);
    }

    public QRecipeEmotion(Path<? extends RecipeEmotion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecipeEmotion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecipeEmotion(PathMetadata metadata, PathInits inits) {
        this(RecipeEmotion.class, metadata, inits);
    }

    public QRecipeEmotion(Class<? extends RecipeEmotion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.recipeInfo = inits.isInitialized("recipeInfo") ? new com.example.foodking.Recipe.RecipeInfo.QRecipeInfo(forProperty("recipeInfo"), inits.get("recipeInfo")) : null;
        this.user = inits.isInitialized("user") ? new com.example.foodking.User.QUser(forProperty("user")) : null;
    }

}

