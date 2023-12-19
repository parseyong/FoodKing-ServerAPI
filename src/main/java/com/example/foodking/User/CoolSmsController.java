package com.example.foodking.User;

import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.User.DTO.PhoneAuthReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = "CoolSMS")
public class CoolSmsController {

    private final CoolSmsService coolSmsService;

    @GetMapping("/messages")
    public ResponseEntity<CommonResDTO> sendMessage(@RequestParam(name = "phoneNum") String phoneNum){

        coolSmsService.sendMessage(phoneNum);
        return ResponseEntity.status(200).body(CommonResDTO.of("HttpStatus.OK","인증번호 전송",null));
    }

    @PostMapping("/messages")
    public ResponseEntity<CommonResDTO> authNumCheck(@RequestBody PhoneAuthReqDTO phoneAuthReqDTO){

        coolSmsService.authenticationMessage(phoneAuthReqDTO);
        return ResponseEntity.status(200).body(CommonResDTO.of("HttpStatus.OK","인증 성공!",null));
    }
}
