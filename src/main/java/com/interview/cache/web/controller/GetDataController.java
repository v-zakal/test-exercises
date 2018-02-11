package com.interview.cache.web.controller;

import com.interview.cache.service.MovieService;
import com.interview.cache.web.protocol.request.MovieByRankRequest;
import com.interview.cache.web.protocol.response.MovieResponse;
import com.interview.cache.web.protocol.response.Response;
import com.interview.cache.web.protocol.response.error.MovieNotFoundResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class GetDataController {
    private final MovieService movieService;

    public GetDataController(MovieService movieService) {
        this.movieService = movieService;
    }



    @PostMapping(path = "/getMovieByRank",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> getData(@RequestBody @Valid MovieByRankRequest request) {

        return movieService.findMovieByRank(request.getRank())
                .map(MovieResponse::new)
                .map(Response.class::cast)
                .map(ResponseEntity.ok()::body)
                .orElse(ResponseEntity.unprocessableEntity()
                        .body(new MovieNotFoundResponse("Movie with rank " + request.getRank() + " not found")));
    }
}
