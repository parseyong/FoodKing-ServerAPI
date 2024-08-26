package com.example.foodking.user.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.user.dto.request.*;
import com.example.foodking.user.dto.response.UserFindRes;
import com.example.foodking.user.service.UserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/login")
    public ResponseEntity<CommonResDTO> login(@RequestBody @Valid LoginReq loginReq){

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResDTO.of("로그인 성공!",userService.login(loginReq)));
    }

    @PostMapping("/users")
    public ResponseEntity<CommonResDTO> addUser(@RequestBody @Valid UserAddReq userAddReq){

        userService.addUser(userAddReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("회원가입 완료",null));
    }

    @GetMapping("/email/check")
    public ResponseEntity<CommonResDTO> checkEmailDuplication(
            @RequestParam(name = "email") @Email(message = "이메일 형식이 올바르지 않습니다") @NotBlank(message = "이메일 정보를 입력해주세요") String email){

        boolean isDuplicated = userService.checkEmailDuplication(email);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이메일 중복체크 완료",isDuplicated));
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<CommonResDTO> checkNickNameDuplication(
            @RequestParam(name = "nickName") @NotBlank(message = "닉네임 정보를 입력해주세요") String nickName){

        boolean isDuplicated = userService.checkNickNameDuplication(nickName);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("닉네임 중복체크 완료",isDuplicated));
    }

    @GetMapping("/users/email/find")
    public ResponseEntity<CommonResDTO> findEmail(
            @RequestParam @NotBlank(message = "전화번호를 입력하세요") String phoneNum){

        String email = userService.findEmail(phoneNum);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("이메일 찾기 성공",email));
    }

    // 새로운 비밀번호를 생성해 반환하므로 patch요청으로 받는다.
    @PatchMapping("/users/password/find")
    public ResponseEntity<CommonResDTO> findPassword(@RequestBody @Valid PasswordFindReq passwordFindReq){

        String password = userService.findPassword(passwordFindReq);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("비밀번호 찾기성공",password));
    }

    @GetMapping("/users")
    public ResponseEntity<CommonResDTO> findUser(final @AuthenticationPrincipal Long userId){

        UserFindRes userFindRes = userService.findUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저정보 조회 성공", userFindRes));
    }

    @PatchMapping("/users")
    public ResponseEntity<CommonResDTO> updateUser(final @AuthenticationPrincipal Long userId,
                                                   @RequestBody @Valid UserUpdateReq userUpdateReq){

        userService.updateUser(userUpdateReq,userId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저정보 변경 성공",null));
    }

    @DeleteMapping("/users")
    public ResponseEntity<CommonResDTO> deleteUser(@RequestBody @Valid UserDeleteReq userDeleteReq,
                                                   final HttpServletRequest request){

        userService.deleteUser(userDeleteReq,request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("유저 삭제완료",null));
    }
}
