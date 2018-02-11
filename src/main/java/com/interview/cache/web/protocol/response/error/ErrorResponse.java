package com.interview.cache.web.protocol.response.error;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.cache.web.protocol.response.Response;

public class ErrorResponse extends Response {
    @JsonProperty
    private final String path;

    public ErrorResponse(int code, String message, String path) {
       super(code, message);
        this.path = path;
    }
}
