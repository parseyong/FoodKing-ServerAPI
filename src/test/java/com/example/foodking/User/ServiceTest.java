package com.example.foodking.User;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.User.DTO.*;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/*
    서비스로직에서 예외가 발생할 경우 ControllerAdvice가 발생한 예외를 핸들링하게 되는데 이러한 테스트는 서비스테스트가 아닌
    컨트롤러테스트에서 처리하는 것이 더 적합하다며 테스트가 중복되어 서비스테스트에는 해당 테스트코드를 작성하지 않는다.
    또한 단순히 Repository메소드 하나만 호출하여 결과를 반환하는 메소드에 대해서는 서비스테스트와 거리가 멀어 테스트코드를 작성하지 않았다.
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
        given(userRepository.findUserByEmail(any())).willReturn(Optional.ofNullable(user));
        given(jwtProvider.createToken(any(),any())).willReturn("token");
        given(passwordEncoder.matches(any(),any())).willReturn(true);

        //when
        String token = userService.login(loginReqDTO);

        //then
        Assertions.assertEquals(token,"token");
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 성공)")
    public void addUserSuccess(){
        //given
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(userRepository.existsByNickName(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");

        //when
        User savedUser =userService.addUser(addUserReqDTO);

        //then
        Assertions.assertEquals("encodedPassword",savedUser.getPassword());
        Assertions.assertEquals("test@google.com", savedUser.getEmail());
        Assertions.assertEquals("nickName", savedUser.getNickName());
        Assertions.assertEquals("01056962173", savedUser.getPhoneNum());
        verify(userRepository).save(savedUser);
    }

    @Test
    @DisplayName("유저정보 조회테스트 -> (조회성공)")
    public void readUserInfoSuccess(){
        //given
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        //when
        ReadUserInfoResDTO readUserInfoResDTO = userService.readUserInfo(any());

        //then
        Assertions.assertEquals("test@google.com",readUserInfoResDTO.getEmail());
        Assertions.assertEquals("nickName",readUserInfoResDTO.getNickName());
        Assertions.assertEquals("01056962173",readUserInfoResDTO.getPhoneNum());
    }

    @Test
    @DisplayName("유저정보 수정 테스트 -> (정보수정 성공)")
    public void updateUserInfoSuccess(){
        //given
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(passwordEncoder.matches(any(),any())).willReturn(true);

        //when
        User updatedUser = userService.updateUserInfo(updateUserInfoReqDTO,any());

        //then
        Assertions.assertEquals("newNickName",updatedUser.getNickName());
        Assertions.assertEquals("encodedPassword",updatedUser.getPassword());
        Assertions.assertEquals("01056962174",updatedUser.getPhoneNum());
        verify(userRepository).save(updatedUser);
    }

    @Test
    @DisplayName("유저 삭제테스트 -> (삭제성공)")
    public void deleteUserInfoSuccess(){
        //given
        given(userRepository.findUserByEmail(any())).willReturn(Optional.ofNullable(user));
        given(passwordEncoder.matches(any(),any())).willReturn(true);

        //when
        userService.deleteUser(deleteUserReqDTO);

        //then
        verify(userRepository).delete(user);
    }

    /*
        서비스로직에서 발생하는 예외에 대해 알맞게 예외변환이 이루어지는지 하나의 테스트코드로 테스트한다.
        예외변환 로직은 모두 동일하며 변환하는 커스텀예외코드만 다르기때문에 불필요하게 모든 예외에 대해 테스트할 필요가 없다.
    */
    @Test
    @DisplayName("예외전환테스트 -> (성공)")
    public void exceptionSuccess(){
        //given
        given(userRepository.findUserByEmail(any())).willReturn(Optional.empty());

        // when, then
        assertThrows(CommondException.class, () -> userService.deleteUser(deleteUserReqDTO));
    }
}
