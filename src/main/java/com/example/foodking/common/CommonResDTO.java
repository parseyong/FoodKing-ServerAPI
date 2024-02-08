package com.example.foodking.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class CommonResDTO<D> {

    private final String message;
    private final D data;
}
