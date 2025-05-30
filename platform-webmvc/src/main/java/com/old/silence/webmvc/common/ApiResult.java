package com.old.silence.webmvc.common;

/**
 * @author moryzang
 */
public class ApiResult<T> {

    private Integer code;
    private String message;
    private T data;

    public ApiResult() {
    }

    public ApiResult(T data) {
        this.code = 200;
        this.message = "";
        this.data = data;

    }

    public ApiResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResult(String message, T data) {
        this.message = message;
        this.data = data;

    }

    public ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return success(null);
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(200);
        apiResult.setMessage("success");
        apiResult.setData(data);
        return apiResult;
    }

    public static <T> ApiResult<T> error(String message) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(500);
        apiResult.setMessage(message);
        return apiResult;
    }

    public static <T> ApiResult<T> error(Integer code, String message) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(code);
        apiResult.setMessage(message);
        return apiResult;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
