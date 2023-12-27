package com.example.foodking.CoolSms;

import com.example.foodking.Common.CommonResDTO;
import com.example.foodking.CoolSms.DTO.PhoneAuthReqDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "CoolSMS")
public class CoolSmsController {

    private final CoolSmsService coolSmsService;

    @GetMapping("/messages")
    public ResponseEntity<CommonResDTO> sendMessage(@RequestParam(name = "phoneNum") @NotBlank String phoneNum){

        coolSmsService.sendMessage(phoneNum);
        return ResponseEntity.status(200).body(CommonResDTO.of("인증번호 전송",null));
    }

    @PostMapping("/messages")
    public ResponseEntity<CommonResDTO> authNumCheck(@RequestBody @Valid PhoneAuthReqDTO phoneAuthReqDTO){

        coolSmsService.authNumCheck(phoneAuthReqDTO);
        return ResponseEntity.status(200).body(CommonResDTO.of("인증 성공!",null));
    }
}
