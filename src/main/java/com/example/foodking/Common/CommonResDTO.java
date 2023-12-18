package com.example.foodking.Common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class CommonResDTO<D> {

    private final String statusCode;
    private final String message;
    private final D data;
}
