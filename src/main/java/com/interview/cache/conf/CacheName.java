package com.interview.cache.conf;

import static com.interview.cache.conf.CacheName.Values.MOVIES_VALUE;

public enum CacheName {
    MOVIES(MOVIES_VALUE);


    private final String name;

    CacheName(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    public static class Values {
        public static final String MOVIES_VALUE = "movies";
    }
}