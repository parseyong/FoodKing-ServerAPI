package com.example.foodking.emotion.dto.req;

import com.example.foodking.emotion.enums.EmotionType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder // 테스트를 위한 빌더추가
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmotionToggleReq {

    @NotNull(message = "이모션 타입을 입력해주세요.")
    private EmotionType emotionType;
}
