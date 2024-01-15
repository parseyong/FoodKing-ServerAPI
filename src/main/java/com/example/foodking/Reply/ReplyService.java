package com.example.foodking.Reply;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeService;
import com.example.foodking.User.User;
import com.example.foodking.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserService userService;
    private final RecipeService recipeService;

    @Transactional
    public void addReply(Long userId, Long recipeInfoId, String content){
        User user = userService.findUserById(userId);

        RecipeInfo recipeInfo = recipeService.findRecipeInfoById(recipeInfoId);

        Reply reply = Reply.builder()
                .content(content)
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        replyRepository.save(reply);
    }
}
