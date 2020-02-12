package com.hz.domain;

public class HttpResult {
    // 响应的状态码
    private int code;

    public HttpResult(int code, String body) {
        this.code = code;
        this.body = body;
    }

    // 响应的响应体
    private String body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
