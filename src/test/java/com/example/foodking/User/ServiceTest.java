package com.example.foodking.User;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.User.DTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/*
    단순히 Repository메소드 하나만 호출하여 결과를 반환하는 메소드에 대해서는 서비스테스트와 거리가 멀어 테스트코드를 작성하지 않았다.
    ex) UserService.emailDuplicatedChecking()
*/
@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    private User user;
    private LoginReqDTO loginReqDTO;
    private AddUserReqDTO addUserReqDTO;
    private UpdateUserInfoReqDTO updateUserInfoReqDTO;
    private DeleteUserReqDTO deleteUserReqDTO;

    @BeforeEach
    void beforeEach(){
        System.out.println("beforeEach process");
        this.user = User.builder()
                .email("test@google.com")
                .password("1234")
                .nickName("nickName")
                .phoneNum("01056962173")
                .build();
        this.loginReqDTO = LoginReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();
        this.addUserReqDTO= AddUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .passwordRepeat("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();
        this.updateUserInfoReqDTO= UpdateUserInfoReqDTO.builder()
                .nickName("newNickName")
                .oldPassword("1234")
                .newPassword("12345")
                .phoneNum("01056962174")
                .build();
        this.deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();
    }

    @Test
    @DisplayName("로그인 테스트 -> (로그인 성공)")
    public void loginSuccess(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));
        given(jwtProvider.createToken(any(),any())).willReturn("token");
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

        //when
        String token = userService.login(loginReqDTO);

        //then
        assertThat(token).isEqualTo("token");
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        verify(jwtProvider,times(1)).createToken(any(),any());
    }

    @Test
    @DisplayName("로그인 테스트 -> (실패 : 존재하지 않는 유저)")
    public void loginFail1(){
        //given
        given(userRepository.findUserByEmail(loginReqDTO.getEmail())).willReturn(Optional.empty());

        // when, then
        try{
            userService.login(loginReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
            verify(jwtProvider,times(0)).createToken(any(),any());
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.LOGIN_FAIL);
        }
    }

    @Test
    @DisplayName("로그인 테스트 -> (실패 : 비밀번호 불일치)")
    public void loginFail2(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));

        // when, then
        try{
            userService.login(loginReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(jwtProvider,times(0)).createToken(any(),any());
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.LOGIN_FAIL);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 성공)")
    public void addUserSuccess(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);
        given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");

        //when
        userService.addUser(addUserReqDTO);

        //then
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 이메일 중복)")
    public void addUserFail1(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(true);

        // when, then
        try{
            userService.addUser(addUserReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.EMAIL_DUPLICATED);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 닉네임 중복)")
    public void addUserFail2(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(true);

        // when, then
        try{
            userService.addUser(addUserReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NICKNAME_DUPLICATED);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 비밀번호 불일치)")
    public void addUserFail3(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);

        // when, then
        try{
            addUserReqDTO.setPassword("passwordNotCollectTest");
            userService.addUser(addUserReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
        }
    }

    @Test
    @DisplayName("이메일 찾기테스트 -> (성공)")
    public void findEmailTestSuccess(){
        //given
        String emailInfo = "test@google.com";
        given(userRepository.findEmailByPhoneNum(any(String.class))).willReturn(emailInfo.describeConstable());

        //when
        String result = userService.findEmail("01056962173");

        //then
        assertThat(result).isEqualTo(emailInfo);
    }

    @Test
    @DisplayName("이메일 찾기테스트 -> (실패 : 존재하지 않는 유저)")
    public void findEmailTestFail1(){
        //given
        given(userRepository.findEmailByPhoneNum(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            String result = userService.findEmail("01056962173");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("비밀번호 찾기테스트 -> (성공)")
    public void findPasswordTestSuccess(){
        //given
        String passwordInfo = "1234";
        given(userRepository.findPasswordByEmail(any(String.class))).willReturn(passwordInfo.describeConstable());

        //when
        String result = userService.findPassword("Test@google.com");

        //then
        assertThat(result).isEqualTo(passwordInfo);
    }

    @Test
    @DisplayName("비밀번호 찾기테스트 -> (실패 : 존재하지 않는 유저)")
    public void findPasswordTestFail1(){
        //given
        given(userRepository.findPasswordByEmail(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            String result = userService.findPassword("Test@google.com");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("유저정보 조회테스트 -> (조회성공)")
    public void readUserInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        ReadUserInfoResDTO readUserInfoResDTO = userService.readUserInfo(1l);

        //then
        assertThat(readUserInfoResDTO.getEmail()).isEqualTo("test@google.com");
        assertThat(readUserInfoResDTO.getNickName()).isEqualTo("nickName");
        assertThat(readUserInfoResDTO.getPhoneNum()).isEqualTo("01056962173");
    }

    @Test
    @DisplayName("유저정보 조회테스트 -> (실패 : 존재하지 않는 유저)")
    public void readUserInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.readUserInfo(1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("유저정보 수정 테스트 -> (정보수정 성공)")
    public void updateUserInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

        //when
        User updatedUser = userService.updateUserInfo(updateUserInfoReqDTO,1l);

        //then
        assertThat(updatedUser.getNickName()).isEqualTo("newNickName");
        assertThat(updatedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(updatedUser.getPhoneNum()).isEqualTo("01056962174");
        verify(passwordEncoder,times(1)).encode(any(String.class));
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        verify(userRepository,times(1)).save(any(User.class));
    }
    
    @Test
    @DisplayName("유저정보 수정테스트 -> (실패 : 존재하지 않는 유저)")
    public void updateUserInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.updateUserInfo(updateUserInfoReqDTO,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
        }
    }

    @Test
    @DisplayName("유저정보 수정테스트 -> (실패 : 비밀번호 불일치)")
    public void updateUserInfoFail2(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(false);

        // when, then
        try{
            userService.updateUserInfo(updateUserInfoReqDTO,1l);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
        }
    }

    @Test
    @DisplayName("유저 삭제테스트 -> (삭제성공)")
    public void deleteUserInfoSuccess(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

        //when
        userService.deleteUser(deleteUserReqDTO);

        //then
        verify(userRepository,times(1)).delete(any(User.class));
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
    }
    
    @Test
    @DisplayName("유저 삭제테스트 -> (실패 : 존재하지 않는 유저)")
    public void deleteUserInfoFail1(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.deleteUser(deleteUserReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(userRepository,times(0)).delete(any(User.class));
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
        }
    }

    @Test
    @DisplayName("유저 삭제테스트 -> (실패 : 비밀번호 불일치)")
    public void deleteUserInfoFail2(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(false);

        // when, then
        try{
            userService.deleteUser(deleteUserReqDTO);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
            verify(userRepository,times(0)).delete(any(User.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        }
    }
    //isMatchPassword()메소드는 위 테스트에서 실행이 되어 테스트가 같이 진행되므로 작성하지 않음.

    @Test
    @DisplayName("DTO와 엔티티간 변환 테스트")
    public void toEntityAndToDtoTest(){
        User savedUser = AddUserReqDTO.toEntity(addUserReqDTO);
        assertThat(savedUser.getPassword()).isEqualTo("1234");
        assertThat(savedUser.getEmail()).isEqualTo("test@google.com");
        assertThat(savedUser.getNickName()).isEqualTo("nickName");
        assertThat(savedUser.getPhoneNum()).isEqualTo("01056962173");
        System.out.println("AddUserReqDTO -> User 변환성공");

        ReadUserInfoResDTO readUserInfoResDTO = ReadUserInfoResDTO.toDTO(user);
        assertThat(readUserInfoResDTO.getEmail()).isEqualTo("test@google.com");
        assertThat(readUserInfoResDTO.getNickName()).isEqualTo("nickName");
        assertThat(readUserInfoResDTO.getPhoneNum()).isEqualTo("01056962173");
        System.out.println("User -> ReadUserInfoResDTO 변환성공");
    }
}
