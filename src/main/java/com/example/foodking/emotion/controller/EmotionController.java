package com.example.foodking.emotion.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.emotion.common.EmotionType;
import com.example.foodking.emotion.service.EmotionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "이모션")
@RestController
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/replies/{replyId}/emotions")
    public ResponseEntity<CommonResDTO> toggleReplyEmotion(final @AuthenticationPrincipal Long userId,
                                                           final @PathVariable Long replyId,
                                                           final @RequestParam EmotionType emotionType){

        emotionService.toggleReplyEmotion(userId,replyId,emotionType);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글이모션 토글 완료",null));
    }

    @PostMapping("/recipes/{recipeInfoId}/emotions")
    public ResponseEntity<CommonResDTO> toggleRecipeEmotion(final @AuthenticationPrincipal Long userId,
                                                            final @PathVariable Long recipeInfoId,
                                                            final @RequestParam EmotionType emotionType){

        emotionService.toggleRecipeInfoEmotion(userId,recipeInfoId,emotionType);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("레시피이모션 토글 완료",null));
    }
}
