package com.example.foodking.Reply;

import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeService;
import com.example.foodking.User.User;
import com.example.foodking.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private ReplyService replyService;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private UserService userService;
    @Mock
    private RecipeService recipeService;

    private User user;
    private RecipeInfo recipeInfo;

    @BeforeEach
    void beforeEach(){
        this.user = User.builder()
                .email("test@google.com")
                .nickName("test")
                .password("1234")
                .phoneNum("01011111111")
                .build();
        this.recipeInfo = RecipeInfo.builder()
                .user(user)
                .recipeName("testRecipeName")
                .recipeTip("testRecipeTip")
                .build();
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addReplySuccess(){
        //given
        given(userService.findUserById(any(Long.class))).willReturn(this.user);
        given(recipeService.findRecipeInfoById(any(Long.class))).willReturn(this.recipeInfo);

        //when
        replyService.addReply(1l,1l,"댓글테스트");

        //then
        verify(userService,times(1)).findUserById(any(Long.class));
        verify(recipeService,times(1)).findRecipeInfoById(any(Long.class));
        verify(replyRepository,times(1)).save(any(Reply.class));
    }
}
