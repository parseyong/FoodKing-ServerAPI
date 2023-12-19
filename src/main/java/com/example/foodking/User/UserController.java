package com.example.foodking.User;

import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.User.DTO.AddUserReqDTO;
import com.example.foodking.User.DTO.LoginReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "USER")
public class UserController {

    private final UserService userService;
    private final CoolSmsService coolSmsService;

    @PostMapping("/login")
    public ResponseEntity<CommonResDTO> login(@RequestBody LoginReqDTO loginReqDTO){

        String accessToken = userService.login(loginReqDTO);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("HttpStatus.OK","로그인 성공!",accessToken));
    }

    @PostMapping("/users")
    public ResponseEntity<CommonResDTO> addUser(@RequestBody AddUserReqDTO addUserReqDTO){

        coolSmsService.isAuthenticatedNum(addUserReqDTO.getPhoneNum());
        userService.addUser(addUserReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("HttpStatus.CREATED","회원가입 완료",null));
    }

    @GetMapping("/email/check")
    public ResponseEntity<?> emailDuplicatedChecking(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<?> nickNameDuplicatedChecking(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @GetMapping("/email/find")
    public ResponseEntity<?> findEmail(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @GetMapping("/password/find")
    public ResponseEntity<?> findPassword(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @GetMapping("/users")
    public ResponseEntity<?> readUserInfo(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @PatchMapping("/users")
    public ResponseEntity<?> changeUserInfo(){
        return ResponseEntity.status(200).body("gkdl");
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUserInfo(){
        return ResponseEntity.status(200).body("gkdl");
    }
}
