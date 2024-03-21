package com.example.foodking.reply;

import com.example.foodking.emotion.service.EmotionService;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.reply.common.ReplySortType;
import com.example.foodking.reply.domain.Reply;
import com.example.foodking.reply.dto.response.ReadReplyResDTO;
import com.example.foodking.reply.repository.ReplyRepository;
import com.example.foodking.reply.service.ReplyService;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @InjectMocks
    private ReplyService replyService;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private EmotionService emotionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeInfoRepository recipeInfoRepository;

    private User user;
    private RecipeInfo recipeInfo;
    private Reply reply;

    private List<Reply> replyList = new ArrayList<>();

    @BeforeEach
    void beforeEach(){
        this.user = spy(User.builder()
                .email("test@google.com")
                .nickName("test")
                .password("1234")
                .phoneNum("01011111111")
                .build());
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
        given(replyRepository.save(any(Reply.class))).willReturn(reply);
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(recipeInfo));

        //when
        replyService.addReply(1L,1L,"댓글테스트");

        //then
        verify(replyRepository,times(1)).save(any(Reply.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(recipeInfoRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 존재하지 않는 유저")
    public void addReplyFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.addReply(1L,1L,"댓글테스트");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(replyRepository,times(0)).save(any(Reply.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(0)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("댓글 등록 테스트 -> 존재하지 않는 레시피")
    public void addReplyFail2(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(recipeInfoRepository.findById(any(Long.class))).willReturn(Optional.empty());

        //when,then
        try{
            replyService.addReply(1L,1L,"댓글테스트");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_RECIPEINFO);
            verify(replyRepository,times(0)).save(any(Reply.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(recipeInfoRepository,times(1)).findById(any(Long.class));
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> 성공")
    public void updateReplySuccess(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(1L);

        //when
        replyService.updateReply(1L,1L,"수정된 댓글");

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
            replyService.updateReply(1L,1L,"수정된 댓글");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            assertThat(reply.getContent()).isEqualTo("testReplyContent");
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).save(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 수정 테스트 -> (실패 : 댓글 수정권한 없음)")
    public void updateReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(2L);

        //when,then
        try{
            replyService.updateReply(1L,1L,"수정된 댓글");
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
        given(user.getUserId()).willReturn(1L);

        //when
        replyService.deleteReply(1L,1L);

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
            replyService.deleteReply(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 삭제 테스트 -> (실패 : 댓글 삭제권한 없음)")
    public void deleteReplyFail2(){
        //given
        given(replyRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(reply));
        given(user.getUserId()).willReturn(2L);

        //when,then
        try{
            replyService.deleteReply(1L,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_REPLY);
            verify(replyRepository,times(1)).findById(any(Long.class));
            verify(replyRepository,times(0)).delete(any(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 조회 테스트 -> (성공 : 시간순 정렬)")
    public void readReplySuccess1(){
        // given
        reply.changeContent("댓글1");
        Reply reply1 = spy(reply);
        reply.changeContent("댓글2");
        Reply reply2 = spy(reply);
        reply.changeContent("댓글3");
        Reply reply3 = spy(reply);
        RecipeInfo recipeInfoSpy = spy(recipeInfo);

        given(reply1.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,10));
        given(reply2.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,17));
        given(reply3.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,5));
        given(recipeInfoSpy.getReplyList()).willReturn(replyList);
        given(user.getUserId()).willReturn(1L);

        given(emotionService.readReplyEmotionCnt(reply1)).willReturn(3L);
        given(emotionService.readReplyEmotionCnt(reply2)).willReturn(1L);
        given(emotionService.readReplyEmotionCnt(reply3)).willReturn(2L);
        replyList.add(reply1);
        replyList.add(reply2);
        replyList.add(reply3);

        // when
        List<ReadReplyResDTO> result = replyService.readReply(recipeInfoSpy,1L, ReplySortType.LATEST);

        // then
        verify(emotionService,times(3)).readReplyEmotionCnt(any(Reply.class));
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getRegDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,5));
        assertThat(result.get(1).getRegDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,10));
        assertThat(result.get(2).getRegDate()).isEqualTo(LocalDateTime.of(2024,02,02,06,17));
        assertThat(result.get(0).isMyReply()).isTrue();
        assertThat(result.get(1).isMyReply()).isTrue();
        assertThat(result.get(2).isMyReply()).isTrue();
    }

    @Test
    @DisplayName("댓글 조회 테스트 -> (성공 : 좋아요순 정렬)")
    public void readReplySuccess2(){
        // given
        reply.changeContent("댓글1");
        Reply reply1 = spy(reply);
        reply.changeContent("댓글2");
        Reply reply2 = spy(reply);
        reply.changeContent("댓글3");
        Reply reply3 = spy(reply);
        RecipeInfo recipeInfoSpy = spy(recipeInfo);

        given(reply1.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,10));
        given(reply2.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,17));
        given(reply3.getRegDate()).willReturn(LocalDateTime.of(2024,02,02,06,5));
        given(recipeInfoSpy.getReplyList()).willReturn(replyList);
        given(user.getUserId()).willReturn(1L);

        given(emotionService.readReplyEmotionCnt(reply1)).willReturn(3L);
        given(emotionService.readReplyEmotionCnt(reply2)).willReturn(1L);
        given(emotionService.readReplyEmotionCnt(reply3)).willReturn(2L);
        replyList.add(reply1);
        replyList.add(reply2);
        replyList.add(reply3);

        // when
        List<ReadReplyResDTO> result = replyService.readReply(recipeInfoSpy,1L,ReplySortType.LIKE);

        // then
        verify(emotionService,times(3)).readReplyEmotionCnt(any(Reply.class));
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getEmotionCnt()).isEqualTo(3L);
        assertThat(result.get(1).getEmotionCnt()).isEqualTo(2L);
        assertThat(result.get(2).getEmotionCnt()).isEqualTo(1L);
        assertThat(result.get(0).isMyReply()).isTrue();
        assertThat(result.get(1).isMyReply()).isTrue();
        assertThat(result.get(2).isMyReply()).isTrue();
    }
}
