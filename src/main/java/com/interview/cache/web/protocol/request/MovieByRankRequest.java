package com.interview.cache.web.protocol.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class MovieByRankRequest {
    @Min(1)
    @Max(255)
    private final int rank;

    @JsonCreator
    public MovieByRankRequest(@JsonProperty(value = "rank", required = true) int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
