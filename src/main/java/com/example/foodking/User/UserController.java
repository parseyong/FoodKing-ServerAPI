package com.example.foodking.User;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.CoolSms.CoolSmsService;
import com.example.foodking.User.DTO.*;
import com.example.foodking.CoolSms.DTO.PhoneAuthReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "USER")
public class UserController {

    private final UserService userService;
    private final CoolSmsService coolSmsService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<CommonResDTO> login(@RequestBody @Valid LoginReqDTO loginReqDTO){

        String accessToken = userService.login(loginReqDTO);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("로그인 성공!",accessToken));
    }

    @PostMapping("/users")
    public ResponseEntity<CommonResDTO> addUser(@RequestBody @Valid AddUserReqDTO addUserReqDTO){

        coolSmsService.isAuthenticatedNum(addUserReqDTO.getPhoneNum());
        userService.addUser(addUserReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("회원가입 완료",null));
    }

    @GetMapping("/email/check")
    public ResponseEntity<CommonResDTO> emailDuplicatedChecking(@RequestParam(name = "email") @Email @NotBlank String email){

        boolean isDuplicated = userService.emailDuplicatedChecking(email);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이메일 중복체크 완료",isDuplicated));
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<CommonResDTO> nickNameDuplicatedChecking(@RequestParam(name = "nickName") @NotBlank String nickName){

        boolean isDuplicated = userService.nickNameDuplicatedChecking(nickName);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("닉네임 중복체크 완료",isDuplicated));
    }

    @GetMapping("/email/find")
    public ResponseEntity<CommonResDTO> findEmail(@RequestBody @Valid PhoneAuthReqDTO phoneAuthReqDTO){

        coolSmsService.authNumCheck(phoneAuthReqDTO);
        String email = userService.findEmail(phoneAuthReqDTO.getPhoneNum());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이메일 찾기 성공",email));
    }

    @GetMapping("/password/find")
    public ResponseEntity<CommonResDTO> findPassword(@RequestBody @Valid FindPwdReqDTO findPwdReqDTO){

        coolSmsService.authNumCheck(PhoneAuthReqDTO.builder()
                        .phoneNum(findPwdReqDTO.getPhoneNum())
                        .authenticationNumber(findPwdReqDTO.getAuthenticationNumber())
                        .build());
        String password = userService.findPassword(findPwdReqDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("비밀번호 찾기성공",password));
    }

    @GetMapping("/users")
    public ResponseEntity<CommonResDTO> readUserInfo(HttpServletRequest servletRequest){

        Long userId = jwtProvider.readUserIdByToken(servletRequest);
        ReadUserInfoResDTO readUserInfoResDTO = userService.readUserInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저정보 조회 성공",readUserInfoResDTO));
    }

    @PatchMapping("/users")
    public ResponseEntity<CommonResDTO> changeUserInfo(@RequestBody @Valid UpdateUserInfoReqDTO updateUserInfoReqDTO, HttpServletRequest servletRequest){

        Long userId = jwtProvider.readUserIdByToken(servletRequest);
        userService.updateUserInfo(updateUserInfoReqDTO,userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저정보 변경 성공",null));

    }

    @DeleteMapping("/users")
    public ResponseEntity<CommonResDTO> deleteUserInfo(@RequestBody @Valid DeleteUserReqDTO deleteUserReqDTO){

        userService.deleteUser(deleteUserReqDTO);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저 삭제완료",null));
    }
}
