package com.example.foodking.user.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.user.dto.request.AuthNumberCheckReq;
import com.example.foodking.user.dto.request.MessageSendReq;
import com.example.foodking.user.service.CoolSmsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "CoolSMS")
public class CoolSmsController {

    private final CoolSmsService coolSmsService;

    @PostMapping("/message/send")
    public ResponseEntity<CommonResDTO> sendMessage(@RequestBody @Valid MessageSendReq messageSendReq){

        coolSmsService.sendMessage(messageSendReq);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증번호 전송",null));
    }

    @PostMapping("/message/auth")
    public ResponseEntity<CommonResDTO> checkAuthNum(@RequestBody @Valid AuthNumberCheckReq authNumberCheckReq){

        coolSmsService.checkAuthNum(authNumberCheckReq);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증 성공!",null));
    }
}
