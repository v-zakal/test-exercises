package com.interview.cache.service.impl;


import com.interview.cache.integration.redis.util.LockRegistry;
import com.interview.cache.model.dao.Movie;
import com.interview.cache.model.repository.mongo.MovieRepository;
import com.interview.cache.service.MovieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    private static final int MOVIE_RANK = 1;
    private MovieService movieService;
    private MovieRepository movieRepo;

    @Before
    public void init() {
        LockRegistry lockRegistry = new LockRegistry() {
            private final Map<String, Lock> locks = new ConcurrentHashMap<>();

            @Override
            public Lock obtain(Object lockKey) {
                return locks.computeIfAbsent((String) lockKey, k -> new ReentrantLock());
            }
        };

        CacheManager cacheManager = new ConcurrentMapCacheManager();

        movieRepo = mock(MovieRepository.class);

        when(movieRepo.findOneByRank(anyInt())).thenAnswer((Answer<Optional<Movie>>) invocation -> {
            int rank = invocation.getArgument(0);
            return rank > 0 && rank < 255 ?
                    Optional.of(new Movie(rank, "Title of movie of rank " + rank, 1878 + rank))
                    : Optional.empty();
        });

        movieService = new MovieServiceImpl(lockRegistry, movieRepo, cacheManager);
    }

    @Test
   public void cacheIsEmptyWeRequestDataFromDB() {
        final Optional<Movie> movieOpt = movieService.findMovieByRank(MOVIE_RANK);
        assertNotNull(movieOpt);
        assertTrue(movieOpt.isPresent());

        verify(movieRepo).findOneByRank(MOVIE_RANK);
    }

    @Test
    public void cacheIsAutoFilledWeDoNotRequestDataFromDBForSecondRequest() {
        final Optional<Movie> movieOpt = movieService.findMovieByRank(MOVIE_RANK);
        assertNotNull(movieOpt);
        assertTrue(movieOpt.isPresent());

        verify(movieRepo).findOneByRank(MOVIE_RANK);

        final Optional<Movie> movieCachedOpt = movieService.findMovieByRank(MOVIE_RANK);
        assertNotNull(movieCachedOpt);
        assertTrue(movieCachedOpt.isPresent());

        assertEquals(movieOpt.get(), movieCachedOpt.get());
        verify(movieRepo).findOneByRank(MOVIE_RANK);
    }
}