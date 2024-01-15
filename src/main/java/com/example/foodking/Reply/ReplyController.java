package com.example.foodking.Reply;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Common.CommonResDTO;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@Api(tags = "Reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{recipeInfoId}/replys")
    public ResponseEntity<CommonResDTO> addReply(@PathVariable Long recipeInfoId, @RequestBody @NotBlank String content){
        Long userId = JwtProvider.getUserId();
        replyService.addReply(userId,recipeInfoId,content);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResDTO.of("댓글 등록완료",null));
    }

}
