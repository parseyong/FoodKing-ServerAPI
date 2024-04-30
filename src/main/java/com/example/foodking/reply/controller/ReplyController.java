package com.example.foodking.reply.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.reply.dto.request.SaveReplyContentReq;
import com.example.foodking.reply.service.ReplyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@Api(tags = "Reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{recipeInfoId}/replys")
    public ResponseEntity<CommonResDTO> addReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long recipeInfoId,
            @RequestBody @Valid SaveReplyContentReq saveReplyContentReq){

        Long replyId = replyService.addReply(userId,recipeInfoId, saveReplyContentReq.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",replyId));
    }

    @PatchMapping("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> updateReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long replyId,
            @RequestBody @Valid SaveReplyContentReq saveReplyContentReq){

        replyService.updateReply(userId,replyId, saveReplyContentReq.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 수정완료",null));
    }

    @DeleteMapping ("/replys/{replyId}")
    public ResponseEntity<CommonResDTO> deleteReply(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long replyId){

        replyService.deleteReply(userId,replyId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 삭제완료",null));
    }
}
