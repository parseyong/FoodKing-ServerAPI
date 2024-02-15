package com.example.foodking.emotion.controller;

import com.example.foodking.auth.JwtProvider;
import com.example.foodking.common.CommonResDTO;
import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.service.EmotionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api(value = "이모션")
@RestController
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/replys/emotions/{replyId}")
    public ResponseEntity<?> toggleReplyEmotion(@PathVariable final Long replyId
            , @RequestParam @NotNull(message = "이모션 정보를 입력해주세요") EmotionType emotionType){
        final Long userId = JwtProvider.getUserId();
        emotionService.toggleReplyEmotion(userId,replyId,emotionType);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글이모션 토글 완료",null));
    }

    @PostMapping("/recipes/emotions/{recipeInfoId}")
    public ResponseEntity<?> toggleRecipeEmotion(@PathVariable final Long recipeInfoId
            , @RequestParam @NotNull(message = "이모션 정보를 입력해주세요") EmotionType emotionType){
        final Long userId = JwtProvider.getUserId();
        emotionService.toggleRecipeInfoEmotion(userId,recipeInfoId,emotionType);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피이모션 토글 완료",null));
    }
}
