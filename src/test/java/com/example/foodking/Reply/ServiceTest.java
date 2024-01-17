package com.example.foodking.Reply;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
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
    private UserRepository userRepository;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;

    private User user;
    private RecipeInfo recipeInfo;
    private Reply reply;

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
        this.reply = Reply.builder()
                .user(user)
                .recipeInfo(recipeInfo)
                .content("testReplyContent")
                .build();
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 성공")
    public void addReplySuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(this.user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(this.recipeInfo));
        given(replyRepository.save(any(Reply.class))).willReturn(reply);

        //when
        replyService.addReply(1l,1l,"댓글테스트");

        //then
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).save(any(Reply.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> 성공")
    public void updateReplySuccess(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        //when
        replyService.updateReply(null,1l,"수정된 댓글");

        //then
        assertThat(reply.getContent()).isEqualTo("수정된 댓글");
        verify(replyRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).save(any(Reply.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void updateReplyFail1(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.updateReply(null,1l,"수정된 댓글");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            assertThat(reply.getContent()).isEqualTo("testReplyContent");
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).save(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 댓글 수정권한이 없음)")
    public void updateReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        //when,then
        try{
            replyService.updateReply(1l,1l,"수정된 댓글");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_REPLY);
            assertThat(reply.getContent()).isEqualTo("testReplyContent");
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).save(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> 성공")
    public void deleteReplySuccess(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        //when
        replyService.deleteReply(null,1l);

        //then
        verify(replyRepository,times(1)).findById(any(Long.class));
        verify(replyRepository,times(1)).delete(any(Reply.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 존재하지 않는 댓글)")
    public void deleteReplyFail1(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.deleteReply(null,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 댓글 삭제권한이 없음)")
    public void deleteReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));

        //when,then
        try{
            replyService.deleteReply(1l,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }
}
