package com.example.foodking.user;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.domain.User;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.LoginTokenRes;
import com.example.foodking.user.dto.response.UserReadRes;
import com.example.foodking.user.repository.UserRepository;
import com.example.foodking.user.service.CoolSmsService;
import com.example.foodking.user.service.UserService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

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
    @Mock
    private CoolSmsService coolSmsService;

    private User user;
    private LoginReq loginReq;
    private UserAddReq userAddReq;
    private UserUpdateReq userUpdateReq;
    private UserDeleteReq userDeleteReq;
    private PasswordFindReq passwordFindReq;

    @BeforeEach
    void beforeEach(){
        this.user = User.builder()
                .email("test@google.com")
                .password("1234")
                .nickName("nickName")
                .phoneNum("01056962173")
                .build();
        this.loginReq = LoginReq.builder()
                .email("test@google.com")
                .password("1234")
                .build();
        this.userAddReq = UserAddReq.builder()
                .email("test@google.com")
                .password("1234")
                .passwordRepeat("1234")
                .phoneNum("01056962173")
                .nickName("nickName")
                .build();
        this.userUpdateReq = UserUpdateReq.builder()
                .nickName("newNickName")
                .oldPassword("1234")
                .newPassword("12345")
                .build();
        this.userDeleteReq = UserDeleteReq.builder()
                .email("test@google.com")
                .password("1234")
                .build();
        this.passwordFindReq = PasswordFindReq.builder()
                .email("test@google.com")
                .phoneNum("01056962173")
                .build();
    }

    @Test
    @DisplayName("로그인 테스트 -> (로그인 성공)")
    public void loginSuccess(){
        //given
        User spyUser = spy(user);
        given(spyUser.getUserId()).willReturn(1L);

        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(spyUser));
        given(jwtProvider.createAccessToken(any(Long.class),any())).willReturn("accessToken");
        given(jwtProvider.createRefreshToken(any(Long.class),any())).willReturn("refreshToken");
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

        //when
        LoginTokenRes loginTokenRes = userService.login(loginReq);

        //then
        assertThat(loginTokenRes.getAccessToken()).isEqualTo("accessToken");
        assertThat(loginTokenRes.getRefreshToken()).isEqualTo("refreshToken");
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        verify(userRepository,times(1)).findUserByEmail(any(String.class));
        verify(jwtProvider,times(1)).createAccessToken(any(Long.class),any());
        verify(jwtProvider,times(1)).createRefreshToken(any(Long.class),any());
    }

    @Test
    @DisplayName("로그인 테스트 -> (실패 : 존재하지 않는 유저)")
    public void loginFail1(){
        //given
        given(userRepository.findUserByEmail(loginReq.getEmail())).willReturn(Optional.empty());

        // when, then
        try{
            userService.login(loginReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
            verify(jwtProvider,times(0)).createAccessToken(any(Long.class),any());
            verify(jwtProvider,times(0)).createRefreshToken(any(Long.class),any());
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.LOGIN_FAIL);
        }
    }

    @Test
    @DisplayName("로그인 테스트 -> (실패 : 비밀번호 불일치)")
    public void loginFail2(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(false);

        // when, then
        try{
            userService.login(loginReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(jwtProvider,times(0)).createAccessToken(any(Long.class),any());
            verify(jwtProvider,times(0)).createRefreshToken(any(Long.class),any());
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.LOGIN_FAIL);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 성공)")
    public void addUserSuccess(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);
        given(userRepository.existsByPhoneNum(any(String.class))).willReturn(false);

        //when
        userService.addUser(userAddReq);

        //then
        verify(userRepository,times(1)).save(any(User.class));
        verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
        verify(coolSmsService,times(1)).deleteAuthInfo(any(String.class));
        verify(userRepository,times(1)).existsByNickName(any(String.class));
        verify(userRepository,times(1)).existsByEmail(any(String.class));
        verify(userRepository,times(1)).existsByPhoneNum(any(String.class));
        verify(passwordEncoder,times(1)).encode(any(String.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 이메일 중복)")
    public void addUserFail1(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(true);

        // when, then
        try{
            userService.addUser(userAddReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            verify(userRepository,times(0)).existsByNickName(any(String.class));
            verify(userRepository,times(1)).existsByEmail(any(String.class));
            verify(userRepository,times(0)).existsByPhoneNum(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
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
            userService.addUser(userAddReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            verify(userRepository,times(1)).existsByNickName(any(String.class));
            verify(userRepository,times(1)).existsByEmail(any(String.class));
            verify(userRepository,times(0)).existsByPhoneNum(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NICKNAME_DUPLICATED);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 비밀번호 불일치)")
    public void addUserFail3(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);
        given(userRepository.existsByPhoneNum(any(String.class))).willReturn(false);

        // when, then
        try{
            userAddReq.setPassword("passwordNotCollectTest");
            userService.addUser(userAddReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            verify(userRepository,times(1)).existsByNickName(any(String.class));
            verify(userRepository,times(1)).existsByEmail(any(String.class));
            verify(userRepository,times(1)).existsByPhoneNum(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
        }
    }

    @Test
    @DisplayName("회원가입 테스트 -> (실패 : 중복된 전화번호)")
    public void addUserFail4(){
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(false);
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);
        given(userRepository.existsByPhoneNum(any(String.class))).willReturn(true);

        // when, then
        try{
            userService.addUser(userAddReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            verify(userRepository,times(1)).existsByNickName(any(String.class));
            verify(userRepository,times(1)).existsByEmail(any(String.class));
            verify(userRepository,times(1)).existsByPhoneNum(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PHONE_NUMBER_DUPLICATED);
        }
    }

    @Test
    @DisplayName("이메일 찾기테스트 -> (성공)")
    public void findEmailTestSuccess(){
        //given
        given(userRepository.findEmailByPhoneNum(any(String.class)))
                .willReturn("test@google.com".describeConstable());

        //when
        String result = userService.findEmail("01056962173");

        //then
        verify(userRepository,times(1)).findEmailByPhoneNum(any(String.class));
        verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
        verify(coolSmsService,times(1)).deleteAuthInfo(any(String.class));
        assertThat(result).isEqualTo("test@google.com");
    }

    @Test
    @DisplayName("이메일 찾기테스트 -> (실패 : 존재하지 않는 이메일)")
    public void findEmailTestFail1(){
        //given
        given(userRepository.findEmailByPhoneNum(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.findEmail("01056962173");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findEmailByPhoneNum(any(String.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("비밀번호 찾기테스트 -> (성공)")
    public void findPasswordTestSuccess(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));

        //when
        userService.findPassword(passwordFindReq);

        //then
        verify(userRepository,times(1)).findUserByEmail(any(String.class));
        verify(passwordEncoder,times(1)).encode(any(String.class));
        verify(userRepository,times(1)).save(any(User.class));
        verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
        verify(coolSmsService,times(1)).deleteAuthInfo(any(String.class));
    }

    @Test
    @DisplayName("비밀번호 찾기테스트 -> (실패 : 존재하지 않는 유저)")
    public void findPasswordTestFail1(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.findPassword(passwordFindReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
        }
    }

    @Test
    @DisplayName("비밀번호 찾기테스트 -> (실패 : 유저에 권한없음.)")
    public void findPasswordTestFail2(){
        //given, DB에 저장된 전화번호와 입력받은 전화번호가 다를 경우
        User user = User.builder()
                .email("test@google.com")
                .password("1234")
                .nickName("nickName")
                .phoneNum("01011111111")
                .build();
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(user));

        // when, then
        try{
            userService.findPassword(passwordFindReq);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            verify(coolSmsService,times(1)).isAuthenticatedNum(any(String.class));
            verify(coolSmsService,times(0)).deleteAuthInfo(any(String.class));
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.ACCESS_FAIL_USER);
        }
    }

    @Test
    @DisplayName("유저정보 조회테스트 -> (조회성공)")
    public void findUserInfoSuccess(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));

        //when
        UserReadRes userReadRes = userService.findUser(1L);

        //then
        assertThat(userReadRes.getEmail()).isEqualTo("test@google.com");
        assertThat(userReadRes.getNickName()).isEqualTo("nickName");
        assertThat(userReadRes.getPhoneNum()).isEqualTo("01056962173");
        verify(userRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("유저정보 조회테스트 -> (실패 : 존재하지 않는 유저)")
    public void findUserInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.findUser(1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            verify(userRepository,times(1)).findById(any(Long.class));
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
        given(userRepository.existsByNickName(any(String.class))).willReturn(false);

        //when
        userService.updateUser(userUpdateReq,1L);

        //then
        assertThat(user.getNickName()).isEqualTo("newNickName");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder,times(1)).encode(any(String.class));
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        verify(userRepository,times(1)).findById(any(Long.class));
        verify(userRepository,times(1)).save(any(User.class));
        verify(userRepository,times(1)).existsByNickName(any(String.class));
    }
    
    @Test
    @DisplayName("유저정보 수정테스트 -> (실패 : 존재하지 않는 유저)")
    public void updateUserInfoFail1(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.updateUser(userUpdateReq,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(0)).existsByNickName(any(String.class));
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
            userService.updateUser(userUpdateReq,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(0)).existsByNickName(any(String.class));
        }
    }

    @Test
    @DisplayName("유저정보 수정테스트 -> (실패 : 닉네임 중복)")
    public void updateUserInfoFail3(){
        //given
        given(userRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);
        given(userRepository.existsByNickName(any(String.class))).willReturn(true);

        // when, then
        try{
            userService.updateUser(userUpdateReq,1L);
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NICKNAME_DUPLICATED);
            verify(passwordEncoder,times(0)).encode(any(String.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(userRepository,times(0)).save(any(User.class));
            verify(userRepository,times(1)).findById(any(Long.class));
            verify(userRepository,times(1)).existsByNickName(any(String.class));
        }
    }

    @Test
    @DisplayName("유저 삭제테스트 -> (삭제성공)")
    public void deleteUserInfoSuccess(){
        //given
        User spyUser = spy(user);
        given(spyUser.getUserId()).willReturn(1L);
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.ofNullable(spyUser));
        given(passwordEncoder.matches(any(String.class),any(String.class))).willReturn(true);

        //when
        userService.deleteUser(userDeleteReq, "accessToken");

        //then
        verify(userRepository,times(1)).delete(any(User.class));
        verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
        verify(userRepository,times(1)).findUserByEmail(any(String.class));
        verify(jwtProvider,times(1)).logOut(any(Long.class),any(String.class));
    }
    
    @Test
    @DisplayName("유저 삭제테스트 -> (실패 : 존재하지 않는 유저)")
    public void deleteUserInfoFail1(){
        //given
        given(userRepository.findUserByEmail(any(String.class))).willReturn(Optional.empty());

        // when, then
        try{
            userService.deleteUser(userDeleteReq, "accessToken");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.NOT_EXIST_USER);
            verify(userRepository,times(0)).delete(any(User.class));
            verify(passwordEncoder,times(0)).matches(any(String.class),any(String.class));
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(jwtProvider,times(0)).logOut(any(Long.class),any(String.class));
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
            userService.deleteUser(userDeleteReq, "accessToken");
            fail("예외가 발생하지 않음");
        }catch (CommondException ex){
            assertThat(ex.getExceptionCode()).isEqualTo(ExceptionCode.PASSWORD_NOT_COLLECT);
            verify(userRepository,times(0)).delete(any(User.class));
            verify(passwordEncoder,times(1)).matches(any(String.class),any(String.class));
            verify(userRepository,times(1)).findUserByEmail(any(String.class));
            verify(jwtProvider,times(0)).logOut(any(Long.class),any(String.class));
        }
    }
}
