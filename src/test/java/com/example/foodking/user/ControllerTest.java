package com.example.foodking.user;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.config.SecurityConfig;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.user.controller.UserController;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.ReadUserInfoResDTO;
import com.example.foodking.user.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static com.example.foodking.exception.ExceptionCode.SMS_NOT_AUTHENTICATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = UserController.class)
@Import({SecurityConfig.class, JwtProvider.class})
public class ControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private JwtProvider jwtProvider;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("로그인 테스트 -> (로그인성공)")
    public void loginTestSuccess() throws Exception {
        //given
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();
        String accessToken = "test";
        given(userService.login(any(LoginReqDTO.class))).willReturn(accessToken);

        Gson gson = new Gson();
        String requestBody = gson.toJson(loginReqDTO);

        //when,then
        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(accessToken))
                .andExpect(jsonPath("$.message").value("로그인 성공!"))
                .andDo(print());

        verify(userService,times(1)).login(any(LoginReqDTO.class));
    }

    @Test
    @DisplayName("로그인 테스트 -> (로그인실패 : 입력값 공백)")
    public void loginTestFail1() throws Exception {
        //given
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("")
                .password("")
                .build();
        String accessToken = "test";
        given(userService.login(any(LoginReqDTO.class))).willReturn(accessToken);

        Gson gson = new Gson();
        String requestBody = gson.toJson(loginReqDTO);

        //when,then
        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("이메일 정보를 입력해주세요"))
                .andExpect(jsonPath("$.data.password").value("비밀번호를 입력해주세요"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).login(any(LoginReqDTO.class));

    }

    @Test
    @DisplayName("로그인 테스트 -> (로그인실패 : 이메일 형식예외)")
    public void loginTestFail2() throws Exception {
        //given
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("testgoogle.com")
                .password("1234")
                .build();
        String accessToken = "test";
        given(userService.login(any(LoginReqDTO.class))).willReturn(accessToken);

        Gson gson = new Gson();
        String requestBody = gson.toJson(loginReqDTO);

        //when,then
        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("이메일 형식이 올바르지 않습니다"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).login(any(LoginReqDTO.class));

    }

    @Test
    @DisplayName("로그인 테스트 -> (로그인실패 : 사용자정보 불일치)")
    public void loginTestFail3() throws Exception {
        //given
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();
        given(userService.login(any(LoginReqDTO.class))).willThrow(new CommondException(ExceptionCode.LOGIN_FAIL));

        Gson gson = new Gson();
        String requestBody = gson.toJson(loginReqDTO);

        //when,then
        this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("로그인에 실패하였습니다."))
                .andDo(print());
        verify(userService,times(1)).login(any(LoginReqDTO.class));

    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 성공)")
    public void addUserSuccess() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("test@google.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("1234")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입 완료"))
                .andDo(print());

        verify(userService,times(1)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 입력값 공백)")
    public void addUserFail1() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("")
                .nickName("")
                .phoneNum("")
                .password("")
                .passwordRepeat("")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("이메일 정보를 입력해주세요"))
                .andExpect(jsonPath("$.data.password").value("비밀번호를 입력해주세요"))
                .andExpect(jsonPath("$.data.passwordRepeat").value("비밀번호를 입력해주세요"))
                .andExpect(jsonPath("$.data.nickName").value("닉네임을 입력해주세요"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력해주세요"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 이메일형식 예외)")
    public void addUserFail2() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("testgoogle.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("1234")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("이메일 형식이 올바르지 않습니다"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 이메일 중복예외)")
    public void addUserFail3() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("test@google.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("1234")
                .build();
        doThrow(new CommondException(ExceptionCode.EMAIL_DUPLICATED)).when(userService).addUser(any(AddUserReqDTO.class));
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("중복된 이메일입니다"))
                .andExpect(jsonPath("$.message").value("중복된 이메일입니다"))
                .andDo(print());
        verify(userService,times(1)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 닉네임 중복예외)")
    public void addUserFail4() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("test@google.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("1234")
                .build();
        doThrow(new CommondException(ExceptionCode.NICKNAME_DUPLICATED)).when(userService).addUser(any(AddUserReqDTO.class));
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.nickName").value("중복된 닉네임입니다"))
                .andExpect(jsonPath("$.message").value("중복된 닉네임입니다"))
                .andDo(print());
        verify(userService,times(1)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 비밀번호 불일치)")
    public void addUserFail5() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("test@google.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("12345")
                .build();

        doThrow(new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT)).when(userService).addUser(any(AddUserReqDTO.class));
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.password").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());
        verify(userService,times(1)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 테스트 -> (회원가입 실패 : 인증되지 않은 번호)")
    public void addUserFail6() throws Exception {
        //given
        AddUserReqDTO addUserReqDTO = AddUserReqDTO.builder()
                .email("test@google.com")
                .nickName("testNickName")
                .phoneNum("01056962173")
                .password("1234")
                .passwordRepeat("1234")
                .build();
        doThrow(new CommondException(SMS_NOT_AUTHENTICATION)).when(userService).addUser(any(AddUserReqDTO.class));
        Gson gson = new Gson();
        String requestBody = gson.toJson(addUserReqDTO);

        //when,then
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.phoneNum").value("인증이 되지않은 번호입니다."))
                .andExpect(jsonPath("$.message").value("인증이 되지않은 번호입니다."))
                .andDo(print());
        verify(userService,times(1)).addUser(any(AddUserReqDTO.class));
    }

    @Test
    @DisplayName("이메일 중복 테스트 -> (중복체크 성공)")
    public void emailDupCheckSuccess() throws Exception {
        //given
        given(userService.emailDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/email/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email","test@google.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("false"))
                .andExpect(jsonPath("$.message").value("이메일 중복체크 완료"))
                .andDo(print());
        verify(userService,times(1)).emailDuplicatedChecking(any(String.class));
    }

    @Test
    @DisplayName("이메일 중복 테스트 -> (중복체크 실패 : 이메일 형식예외)")
    public void emailDupCheckFail1() throws Exception {
        //given
        given(userService.emailDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/email/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email","testgoogle.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다"))
                .andDo(print());
        verify(userService,times(0)).emailDuplicatedChecking(any(String.class));

    }

    @Test
    @DisplayName("이메일 중복 테스트 -> (중복체크 실패 : 이메일값 공백)")
    public void emailDupCheckFail2() throws Exception {
        //given
        given(userService.emailDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/email/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이메일 정보를 입력해주세요"))
                .andDo(print());
        verify(userService,times(0)).emailDuplicatedChecking(any(String.class));

    }

    @Test
    @DisplayName("이메일 중복 테스트 -> (중복체크 실패 : 이메일값 파라미터 없음)")
    public void emailDupCheckFail3() throws Exception {
        //given
        given(userService.emailDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/email/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").value("Required request parameter 'email' for method parameter type String is not present(관리자에게 문의하세요)"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).emailDuplicatedChecking(any(String.class));

    }

    @Test
    @DisplayName("닉네임 중복 테스트 -> (중복체크 성공)")
    public void nickNameDupCheckSuccess() throws Exception {
        //given
        given(userService.nickNameDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/nickname/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickName","testNickName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("false"))
                .andExpect(jsonPath("$.message").value("닉네임 중복체크 완료"))
                .andDo(print());
        verify(userService,times(1)).nickNameDuplicatedChecking(any(String.class));
    }

    @Test
    @DisplayName("닉네임 중복 테스트 -> (중복체크 실패 : 닉네임 공백)")
    public void nickNameDupCheckFail1() throws Exception {
        //given
        given(userService.nickNameDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/nickname/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickName",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("닉네임 정보를 입력해주세요"))
                .andDo(print());
        verify(userService,times(0)).nickNameDuplicatedChecking(any(String.class));

    }

    @Test
    @DisplayName("닉네임 중복 테스트 -> (중복체크 실패 : 닉네임파라미터 없음)")
    public void nickNameDupCheckFail2() throws Exception {
        //given
        given(userService.nickNameDuplicatedChecking(any(String.class))).willReturn(false);

        //when,then
        this.mockMvc.perform(get("/nickname/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.nickName").value("Required request parameter 'nickName' for method parameter type String is not present(관리자에게 문의하세요)"))
                .andDo(print());
        verify(userService,times(0)).nickNameDuplicatedChecking(any(String.class));

    }

    @Test
    @DisplayName("이메일 찾기 테스트 -> (이메일 찾기 성공)")
    public void findEmailSuccess() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);
        given(userService.findEmail(any(PhoneAuthReqDTO.class))).willReturn("test");

        //when,then
        this.mockMvc.perform(get("/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("이메일 찾기 성공"))
                .andExpect(jsonPath("$.data").value("test"))
                .andDo(print());
        verify(userService,times(1)).findEmail(any(PhoneAuthReqDTO.class));
    }

    @Test
    @DisplayName("이메일 찾기 테스트 -> (이메일 찾기 실패 : 요청값 공백)")
    public void findEmailFail1() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("")
                .authenticationNumber("")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);

        //when,then
        this.mockMvc.perform(get("/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.phoneNum").value("전화번호를 입력하세요"))
                .andExpect(jsonPath("$.data.authenticationNumber").value("인증번호를 입력하세요"))
                .andDo(print());
        verify(userService,times(0)).findEmail(phoneAuthReqDTO);
    }

    @Test
    @DisplayName("이메일 찾기 테스트 -> (이메일 찾기 실패 : 인증번호 불일치)")
    public void findEmailFail2() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);
        doThrow(new CommondException(ExceptionCode.SMS_AUTHENTICATION_FAIL)).when(userService).findEmail(any(PhoneAuthReqDTO.class));

        //when,then
        this.mockMvc.perform(get("/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다"))
                .andExpect(jsonPath("$.data.authenticationNumber").value("인증번호가 올바르지 않습니다"))
                .andDo(print());
        verify(userService,times(1)).findEmail(any(PhoneAuthReqDTO.class));
    }

    @Test
    @DisplayName("이메일 찾기 테스트 -> (이메일 찾기 실패 : 존재하지 않는 유저)")
    public void findEmailFail3() throws Exception {
        //given
        PhoneAuthReqDTO phoneAuthReqDTO = PhoneAuthReqDTO.builder()
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(phoneAuthReqDTO);
        given(userService.findEmail(any(PhoneAuthReqDTO.class))).willThrow(new CommondException(ExceptionCode.NOT_EXIST_USER));

        //when,then
        this.mockMvc.perform(get("/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());
        verify(userService,times(1)).findEmail(any(PhoneAuthReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 성공)")
    public void findPasswordSuccess() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("test@google.com")
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        given(userService.findPassword(any(FindPwdReqDTO.class))).willReturn("12345");

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 찾기성공"))
                .andExpect(jsonPath("$.data").value("12345"))
                .andDo(print());
        verify(userService,times(1)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 실패 : 입력값 공백)")
    public void findPasswordFail1() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("")
                .phoneNum("")
                .authenticationNumber("")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        given(userService.findPassword(any(FindPwdReqDTO.class))).willReturn("12345");

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 실패 : 이메일 형식예외)")
    public void findPasswordFail2() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("test")
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        given(userService.findPassword(any(FindPwdReqDTO.class))).willReturn("12345");

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andExpect(jsonPath("$.data.email").value("이메일 형식이 올바르지 않습니다"))
                .andDo(print());
        verify(userService,times(0)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 실패 : 인증번호 불일치)")
    public void findPasswordFail3() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("test@google.com")
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        doThrow(new CommondException(ExceptionCode.SMS_AUTHENTICATION_FAIL)).when(userService).findPassword(any(FindPwdReqDTO.class));

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다"))
                .andExpect(jsonPath("$.data.authenticationNumber").value("인증번호가 올바르지 않습니다"))
                .andDo(print());
        verify(userService,times(1)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 실패 : 존재하지 않는 유저)")
    public void findPasswordFail4() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("test@google.com")
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        given(userService.findPassword(any(FindPwdReqDTO.class))).willThrow(new CommondException(ExceptionCode.NOT_EXIST_USER));

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());
        verify(userService,times(1)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    @DisplayName("비밀번호 찾기 테스트 -> (비밀번호 찾기 실패 : 유저 권한없음)")
    public void findPasswordFail5() throws Exception {
        //given
        FindPwdReqDTO findPwdReqDTO = FindPwdReqDTO.builder()
                .email("test@google.com")
                .phoneNum("01056962173")
                .authenticationNumber("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(findPwdReqDTO);
        given(userService.findPassword(any(FindPwdReqDTO.class))).willThrow(new CommondException(ExceptionCode.ACCESS_FAIL_USER));

        //when,then
        this.mockMvc.perform(get("/password/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 유저에 대한 권한이 없습니다"))
                .andDo(print());
        verify(userService,times(1)).findPassword(any(FindPwdReqDTO.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 조회 테스트 -> (조회 성공)")
    public void readUserInfoSuccess() throws Exception {
        //given
        makeAuthentication();
        ReadUserInfoResDTO readUserInfoResDTO = ReadUserInfoResDTO.builder()
                .nickName("nickName")
                .phoneNum("01056962173")
                .email("test@google.com")
                .build();

        given(userService.readUser(any(Long.class))).willReturn(readUserInfoResDTO);

        //when,then
        this.mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("유저정보 조회 성공"))
                .andExpect(jsonPath("$.data.email").value("test@google.com"))
                .andExpect(jsonPath("$.data.nickName").value("nickName"))
                .andExpect(jsonPath("$.data.phoneNum").value("01056962173"))
                .andDo(print());
        verify(userService,times(1)).readUser(any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("유저정보 조회 테스트 -> (조회 실패 : 인증 실패)")
    public void readUserInfoFail1() throws Exception {
        //given
        ReadUserInfoResDTO readUserInfoResDTO = ReadUserInfoResDTO.builder()
                .nickName("nickName")
                .phoneNum("01056962173")
                .email("test@google.com")
                .build();

        given(userService.readUser(any(Long.class))).willReturn(readUserInfoResDTO);

        //when,then
        this.mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());
        verify(userService,times(0)).readUser(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 조회 테스트 -> (조회 실패 : 존재하지 않는 유저)")
    public void readUserInfoFail2() throws Exception {
        //given
        makeAuthentication();
        given(userService.readUser(any(Long.class))).willThrow(new CommondException(ExceptionCode.NOT_EXIST_USER));

        //when,then
        this.mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());
        verify(userService,times(1)).readUser(any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 수정테스트 -> (수정 성공)")
    public void updateUserInfoSuccess() throws Exception {
        //given
        makeAuthentication();
        UpdateUserInfoReqDTO updateUserInfoReqDTO = UpdateUserInfoReqDTO.builder()
                .phoneNum("01056962173")
                .oldPassword("1234")
                .newPassword("12345")
                .nickName("new NickName")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(updateUserInfoReqDTO);

        //when,then
        this.mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("유저정보 변경 성공"))
                .andDo(print());
        verify(userService,times(1)).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("유저정보 수정테스트 -> (수정 실패 : 인증 실패)")
    public void updateUserInfoFail1() throws Exception {
        //given
        UpdateUserInfoReqDTO updateUserInfoReqDTO = UpdateUserInfoReqDTO.builder()
                .phoneNum("01056962173")
                .oldPassword("1234")
                .newPassword("12345")
                .nickName("new NickName")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(updateUserInfoReqDTO);

        //when,then
        this.mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());
        verify(userService,times(0)).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 수정테스트 -> (수정 실패 : 요청값 공백)")
    public void updateUserInfoFail2() throws Exception {
        //given
        makeAuthentication();
        UpdateUserInfoReqDTO updateUserInfoReqDTO = UpdateUserInfoReqDTO.builder()
                .phoneNum("")
                .oldPassword("")
                .newPassword("")
                .nickName("")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(updateUserInfoReqDTO);

        //when,then
        this.mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 수정테스트 -> (수정 실패 : 비밀번호 불일치)")
    public void updateUserInfoFail3() throws Exception {
        //given
        makeAuthentication();
        UpdateUserInfoReqDTO updateUserInfoReqDTO = UpdateUserInfoReqDTO.builder()
                .phoneNum("01056962173")
                .oldPassword("1234")
                .newPassword("12345")
                .nickName("new NickName")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(updateUserInfoReqDTO);
        doThrow(new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT)).when(userService).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));

        //when,then
        this.mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data.password").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());
        verify(userService,times(1)).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저정보 수정테스트 -> (수정 실패 : 존재하지 않는 유저)")
    public void updateUserInfoFail4() throws Exception {
        //given
        makeAuthentication();
        UpdateUserInfoReqDTO updateUserInfoReqDTO = UpdateUserInfoReqDTO.builder()
                .phoneNum("01056962173")
                .oldPassword("1234")
                .newPassword("12345")
                .nickName("new NickName")
                .build();
        Gson gson = new Gson();
        String requestBody = gson.toJson(updateUserInfoReqDTO);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(userService).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));

        //when,then
        this.mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());
        verify(userService,times(1)).updateUser(any(UpdateUserInfoReqDTO.class),any(Long.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 성공)")
    public void deleteUserInfoSuccess() throws Exception {
        //given
        makeAuthentication();
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("유저 삭제완료"))
                .andDo(print());
        verify(userService,times(1)).deleteUser(any(DeleteUserReqDTO.class));
    }

    @Test
    //@WithAnonymousUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 실패 : 인증실패)")
    public void deleteUserInfoFail1() throws Exception {
        //given
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증에 실패하였습니다"))
                .andDo(print());
        verify(userService,times(0)).deleteUser(any(DeleteUserReqDTO.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 실패 : 비밀번호 불일치)")
    public void deleteUserInfoFail2() throws Exception {
        //given
        makeAuthentication();
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);
        doThrow(new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT)).when(userService).deleteUser(any(DeleteUserReqDTO.class));

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data.password").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());
        verify(userService,times(1)).deleteUser(any(DeleteUserReqDTO.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 실패 : 존재하지 않는 유저)")
    public void deleteUserInfoFail3() throws Exception {
        //given
        makeAuthentication();
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("test@google.com")
                .password("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);
        doThrow(new CommondException(ExceptionCode.NOT_EXIST_USER)).when(userService).deleteUser(any(DeleteUserReqDTO.class));

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 유저입니다"))
                .andDo(print());
        verify(userService,times(1)).deleteUser(any(DeleteUserReqDTO.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 실패 : 요청값 공백)")
    public void deleteUserInfoFail4() throws Exception {
        //given
        makeAuthentication();
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("")
                .password("")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).deleteUser(any(DeleteUserReqDTO.class));
    }

    @Test
    //@WithMockUser
    @DisplayName("유저 삭제테스트 -> (유저삭제 실패 : 이메일 형식예외)")
    public void deleteUserInfoFail5() throws Exception {
        //given
        makeAuthentication();
        DeleteUserReqDTO deleteUserReqDTO = DeleteUserReqDTO.builder()
                .email("testgoogle.com")
                .password("1234")
                .build();

        Gson gson = new Gson();
        String requestBody = gson.toJson(deleteUserReqDTO);

        //when,then
        this.mockMvc.perform(delete("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값입니다"))
                .andDo(print());
        verify(userService,times(0)).deleteUser(any(DeleteUserReqDTO.class));
    }

    public void makeAuthentication(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(1L,"1234",authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
