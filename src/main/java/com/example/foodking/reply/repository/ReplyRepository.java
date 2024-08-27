package com.example.foodking.reply.repository;

import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.reply.domain.Reply;
import org.springframework.data.repository.CrudRepository;

public interface ReplyRepository extends CrudRepository<Reply,Long>, ReplyQdslRepository {

    Long countByRecipeInfo(RecipeInfo recipeInfo);
}
