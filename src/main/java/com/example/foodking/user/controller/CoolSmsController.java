package com.example.foodking.user.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.user.dto.request.PhoneAuthReqDTO;
import com.example.foodking.user.service.CoolSmsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CommonResDTO> sendMessage(
            @RequestParam(name = "phoneNum") @NotBlank(message = "전화번호를 입력해주세요") String phoneNum){

        coolSmsService.sendMessage(phoneNum);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증번호 전송",null));
    }

    @PostMapping("/messages")
    public ResponseEntity<CommonResDTO> authNumCheck(@RequestBody @Valid PhoneAuthReqDTO phoneAuthReqDTO){

        coolSmsService.authNumCheck(phoneAuthReqDTO);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증 성공!",null));
    }
}
