package com.example.foodking.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 634231898L;

    public static final QUser user = new QUser("user");

    public final StringPath email = createString("email");

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    public final ListPath<com.example.foodking.recipe.domain.RecipeInfo, com.example.foodking.recipe.domain.QRecipeInfo> recipeInfoList = this.<com.example.foodking.recipe.domain.RecipeInfo, com.example.foodking.recipe.domain.QRecipeInfo>createList("recipeInfoList", com.example.foodking.recipe.domain.RecipeInfo.class, com.example.foodking.recipe.domain.QRecipeInfo.class, PathInits.DIRECT2);

    public final StringPath roleName = createString("roleName");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

