package com.example.foodking.auth;

import com.example.foodking.common.CommonResDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = "JWT")
public class JwtController {

    private final JwtProvider jwtProvider;

    @PostMapping("/refreshToken/reissue")
    public ResponseEntity<CommonResDTO> reissueToken(
            @RequestParam @NotBlank(message = "refreshToken값을 입력해주세요") String refreshToken){

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResDTO.of("로그인 성공!",jwtProvider.reissueToken(refreshToken)));
    }
}
