package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import static com.example.utils.Const.*;

public record Result<T>(int code, T data, String message) {

    public static <T> Result<T> success(T data) {
        return new Result<>(STATUS_CODE_200, data, "success");
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(STATUS_CODE_200, data, message);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, null, message);
    }

    public static <T> Result<T> forbidden(String message) {
        return failure(STATUS_CODE_403, message);
    }

    public static <T> Result<T> unAuthorize(String message) {
        return failure(STATUS_CODE_401, message);
    }

    public String toJson() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
