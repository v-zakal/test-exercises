package com.interview.cache.web.protocol.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class Response {
    @JsonProperty
    private final int code;

    @JsonProperty
    private final String message;

    public Response() {
        this(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
