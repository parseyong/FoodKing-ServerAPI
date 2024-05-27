package com.example.foodking.user.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.user.dto.request.CheckAuthNumberReq;
import com.example.foodking.user.dto.request.SendAuthNumberReq;
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

    @PostMapping("/messages/send")
    public ResponseEntity<CommonResDTO> sendMessage(
            @RequestBody @Valid SendAuthNumberReq sendAuthNumberReq){

        coolSmsService.sendMessage(sendAuthNumberReq.getPhoneNum());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증번호 전송",null));
    }

    @PostMapping("/messages/auth")
    public ResponseEntity<CommonResDTO> authNumCheck(@RequestBody @Valid CheckAuthNumberReq checkAuthNumberReq){

        coolSmsService.authNumCheck(checkAuthNumberReq);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("인증 성공!",null));
    }
}
