package com.example.foodking.Emotion;

import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotion;
import com.example.foodking.Emotion.RecipeEmotion.RecipeEmotionRepository;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotion;
import com.example.foodking.Emotion.ReplyEmotion.ReplyEmotionRepository;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Reply.Reply;
import com.example.foodking.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private EmotionService emotionService;
    @Mock
    private ReplyEmotionRepository replyEmotionRepository;
    @Mock
    private RecipeEmotionRepository recipeEmotionRepository;

    private User user;
    private Reply reply;
    private RecipeInfo recipeInfo;
    private ReplyEmotion replyEmotion;
    private RecipeEmotion recipeEmotion;

    @BeforeEach
    void beforeEach(){
        this.user= spy(User.builder()
                .email("test@google.com")
                .password("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build());
        this.recipeInfo = spy(RecipeInfo.builder()
                .user(user)
                .build());
        this.reply = Reply.builder()
                .content("replyTest")
                .user(user)
                .recipeInfo(recipeInfo)
                .build();
        this.replyEmotion = ReplyEmotion.builder()
                .reply(reply)
                .user(user)
                .emotionType(EmotionType.Like)
                .build();
        this.recipeEmotion = RecipeEmotion.builder()
                .user(user)
                .recipeInfo(recipeInfo)
                .emotionType(EmotionType.Like)
                .build();
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 추가 성공)")
    public void toggleReplyEmotionSuccess(){
        // given
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.empty());

        // when
        emotionService.toggleReplyEmotion(user,reply,EmotionType.Like);

        // then
        verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
        verify(replyEmotionRepository,times(1)).save(any(ReplyEmotion.class));
        verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 성공)")
    public void toggleReplyEmotionSuccess1(){
        // given
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.ofNullable(replyEmotion));
        given(user.getUserId()).willReturn(1l);

        // when
        emotionService.toggleReplyEmotion(user,reply,EmotionType.Like);

        // then
        verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
        verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
        verify(replyEmotionRepository,times(1)).delete(any(ReplyEmotion.class));
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 실패 : 이모션 권한없음)")
    public void toggleReplyEmotionFail1(){
        // given
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.ofNullable(replyEmotion));
        given(user.getUserId()).willReturn(1l);
        User user1 = spy(User.builder().build());
        given(user1.getUserId()).willReturn(2l);

        // when, then
        try{
            emotionService.toggleReplyEmotion(user1,reply,EmotionType.Like);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
            verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
            verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_EMOTION);
        }
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 추가 성공)")
    public void toggleRecipeEmotionSuccess(){
        // given
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.empty());

        // when
        emotionService.toggleRecipeInfoEmotion(user,recipeInfo,EmotionType.Like);

        // then
        verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
        verify(recipeEmotionRepository,times(1)).save(any(RecipeEmotion.class));
        verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 성공)")
    public void toggleRecipeEmotionSuccess2(){
        // given
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.ofNullable(recipeEmotion));
        given(recipeInfo.getUser().getUserId()).willReturn(1l);

        // when
        emotionService.toggleRecipeInfoEmotion(user,recipeInfo,EmotionType.Like);

        // then
        verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
        verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
        verify(recipeEmotionRepository,times(1)).delete(any(RecipeEmotion.class));
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 실패 : 이모션 권한 없음)")
    public void toggleRecipeEmotionFail1(){
        // given
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.ofNullable(recipeEmotion));
        given(recipeInfo.getUser().getUserId()).willReturn(1l);
        User user1 = spy(User.builder().build());
        given(user1.getUserId()).willReturn(2l);

        // when, then
        try{
            emotionService.toggleRecipeInfoEmotion(user1,recipeInfo,EmotionType.Like);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
            verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
            verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_EMOTION);
        }
    }
}
