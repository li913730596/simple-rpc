package com.common.enums;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public enum RpcResponseCode {
    SUCCESS(200, "调用成功"),
    FAIL(500, "调用方法失败"),
    NOT_FOUND_METHOD(500, "未找到方法"),
    NOT_FOUND_CLASS(500, "未找到类");
    private final Integer code;
    private final String message;
}
