package com.example.foodking.User;

import com.example.foodking.Common.CommonResDTO;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "USER")
public class UserController {

    @PostMapping("/users")
    public ResponseEntity<CommonResDTO> addUser(@RequestBody @Valid LoginReqDTO loginReqDTO){


        return ResponseEntity.status(200).body("gkdl");
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
