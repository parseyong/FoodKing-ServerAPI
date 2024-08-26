package com.example.foodking.emotion;

import com.example.foodking.emotion.domain.RecipeEmotion;
import com.example.foodking.emotion.domain.ReplyEmotion;
import com.example.foodking.emotion.enums.EmotionType;
import com.example.foodking.emotion.repository.RecipeEmotionRepository;
import com.example.foodking.emotion.repository.ReplyEmotionRepository;
import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
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
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;

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
                .emotionType(EmotionType.LIKE)
                .build();
        this.recipeEmotion = RecipeEmotion.builder()
                .user(user)
                .recipeInfo(recipeInfo)
                .emotionType(EmotionType.LIKE)
                .build();
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 추가 성공)")
    public void toggleReplyEmotionSuccess(){
        // given
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.empty());
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        // when
        emotionService.toggleReplyEmotion(1L,1L,EmotionType.LIKE);

        // then
        verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
        verify(replyEmotionRepository,times(1)).save(any(ReplyEmotion.class));
        verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 성공)")
    public void toggleReplyEmotionSuccess1(){
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.ofNullable(replyEmotion));
        given(user.getUserId()).willReturn(1L);

        // when
        emotionService.toggleReplyEmotion(1L,1L,EmotionType.LIKE);

        // then
        verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
        verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
        verify(replyEmotionRepository,times(1)).delete(any(ReplyEmotion.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 실패 : 이모션 권한없음)")
    public void toggleReplyEmotionFail1(){
        // given
        User user1 = spy(User.builder().build());
        given(user.getUserId()).willReturn(1L);
        given(user1.getUserId()).willReturn(2L);
        given(replyEmotionRepository.findByReplyAndUser(any(Reply.class),any(User.class))).willReturn(Optional.ofNullable(replyEmotion));
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user1));
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        // when, then
        try{
            emotionService.toggleReplyEmotion(2L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(replyEmotionRepository,times(1)).findByReplyAndUser(any(Reply.class),any(User.class));
            verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
            verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_EMOTION);
        }
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 실패 : 존재하지 않는 유저)")
    public void toggleReplyEmotionFail2(){
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            emotionService.toggleReplyEmotion(1L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(replyEmotionRepository,times(0)).findByReplyAndUser(any(Reply.class),any(User.class));
            verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
            verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("댓글 이모션 toggle테스트 -> (이모션 삭제 실패 : 존재하지 않는 댓글)")
    public void toggleReplyEmotionFail3(){
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            emotionService.toggleReplyEmotion(1L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(replyEmotionRepository,times(0)).findByReplyAndUser(any(Reply.class),any(User.class));
            verify(replyEmotionRepository,times(0)).save(any(ReplyEmotion.class));
            verify(replyEmotionRepository,times(0)).delete(any(ReplyEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
        }
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 추가 성공)")
    public void toggleRecipeEmotionSuccess(){
        // given
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.empty());
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        // when
        emotionService.toggleRecipeInfoEmotion(1L,1L,EmotionType.LIKE);

        // then
        verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
        verify(recipeEmotionRepository,times(1)).save(any(RecipeEmotion.class));
        verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 성공)")
    public void toggleRecipeEmotionSuccess2(){
        // given
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.ofNullable(recipeEmotion));
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        given(recipeInfo.getUser().getUserId()).willReturn(1L);

        // when
        emotionService.toggleRecipeInfoEmotion(1L,1L,EmotionType.LIKE);

        // then
        verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
        verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
        verify(recipeEmotionRepository,times(1)).delete(any(RecipeEmotion.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 실패 : 이모션 권한 없음)")
    public void toggleRecipeEmotionFail1(){
        // given
        User user1 = spy(User.builder().build());
        given(user1.getUserId()).willReturn(2L);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user1));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));
        given(recipeEmotionRepository.findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class))).willReturn(Optional.ofNullable(recipeEmotion));
        given(recipeInfo.getUser().getUserId()).willReturn(1L);

        // when, then
        try{
            emotionService.toggleRecipeInfoEmotion(2L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeEmotionRepository,times(1)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
            verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
            verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_EMOTION);
        }
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 실패 : 존재하지 않는 유저)")
    public void toggleRecipeEmotionFail2(){
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            emotionService.toggleRecipeInfoEmotion(1L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeEmotionRepository,times(0)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
            verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
            verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("레시피 이모션 toggle테스트 -> (이모션 삭제 실패 : 존재하지 않는 레시피)")
    public void toggleRecipeEmotionFail3(){
        // given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            emotionService.toggleRecipeInfoEmotion(1L,1L,EmotionType.LIKE);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(recipeEmotionRepository,times(0)).findByRecipeInfoAndUser(any(RecipeInfo.class),any(User.class));
            verify(recipeEmotionRepository,times(0)).save(any(RecipeEmotion.class));
            verify(recipeEmotionRepository,times(0)).delete(any(RecipeEmotion.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
        }
    }
}
