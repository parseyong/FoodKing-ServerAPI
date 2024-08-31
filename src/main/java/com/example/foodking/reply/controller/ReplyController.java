package com.example.foodking.reply.controller;

import com.example.foodking.common.CommonResDTO;
import com.example.foodking.reply.dto.request.ReplyAddReq;
import com.example.foodking.reply.dto.request.ReplyUpdateReq;
import com.example.foodking.reply.service.ReplyService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Api(tags = "Reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/recipes/{recipeInfoId}/replies")
    public ResponseEntity<CommonResDTO> addReply(final @AuthenticationPrincipal Long userId,
                                                 final @PathVariable Long recipeInfoId,
                                                 @RequestBody @Valid ReplyAddReq replyAddReq){

        Long replyId = replyService.addReply(userId,recipeInfoId, replyAddReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",replyId));
    }

    @PatchMapping("/replies/{replyId}")
    public ResponseEntity<CommonResDTO> updateReply(final @AuthenticationPrincipal Long userId,
                                                    final @PathVariable Long replyId,
                                                    @RequestBody @Valid ReplyUpdateReq replyUpdateReq){

        replyService.updateReply(userId,replyId, replyUpdateReq);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 수정완료",null));
    }

    @DeleteMapping ("/replies/{replyId}")
    public ResponseEntity<CommonResDTO> deleteReply(final @AuthenticationPrincipal Long userId,
                                                    final @PathVariable Long replyId){

        replyService.deleteReply(userId,replyId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResDTO.of("댓글 삭제완료",null));
    }
}
