package com.acxiom.ams.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseVO<T> {
    private String code;
    private String message;
    private T data;
}
