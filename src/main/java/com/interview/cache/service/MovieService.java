package com.interview.cache.service;

import com.interview.cache.model.dao.Movie;

import java.util.Optional;

public interface MovieService {
    Optional<Movie> findMovieByRank(int rank);
}
