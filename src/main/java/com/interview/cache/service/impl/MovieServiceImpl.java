package com.interview.cache.service.impl;

import com.interview.cache.conf.CacheName;
import com.interview.cache.integration.redis.util.LockRegistry;
import com.interview.cache.model.dao.Movie;
import com.interview.cache.model.repository.mongo.MovieRepository;
import com.interview.cache.service.MovieService;
import com.interview.cache.web.controller.ExceptionHandlingControllersAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Service
public class MovieServiceImpl implements MovieService {
    private final static Logger LOG = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final LockRegistry distributedLockRegistry;
    private final MovieRepository movieRepo;
    private final CacheManager cacheManager;

    @Autowired
    public MovieServiceImpl(LockRegistry distributedLockRegistry, MovieRepository movieRepo, CacheManager cacheManager) {
        this.distributedLockRegistry = distributedLockRegistry;
        this.movieRepo = movieRepo;
        this.cacheManager = cacheManager;
    }

    @Override
    public Optional<Movie> findMovieByRank(int rank) {
        final Optional<Movie> movieByRankInCache = findMovieByRankInCacheSafety(rank);

        return movieByRankInCache.isPresent() ? movieByRankInCache : findMovieByRankWithDbLock(rank);
    }

    private Optional<Movie> findMovieByRankWithDbLock(final int rank) {
        final Lock dbAccessLock = distributedLockRegistry.obtain(String.valueOf(rank));

        try {
            dbAccessLock.lock();
            final Optional<Movie> movieInCacheOpt = findMovieByRankInCacheSafety(rank);
            final Optional<Movie> movieOpt = movieInCacheOpt.isPresent() ?  movieInCacheOpt : movieRepo.findOneByRank(rank);
            movieOpt.ifPresent(movie -> putMovieInCacheSafety(rank, movie));

            return movieOpt;
        } finally {
            dbAccessLock.unlock();
        }
    }

    private Optional<Movie> findMovieByRankInCacheSafety(int rank) {
        try {
            return Optional.ofNullable(cacheManager.getCache(CacheName.MOVIES.getValue()).get(rank))
                    .map(Cache.ValueWrapper::get)
                    .map(Movie.class::cast);
        } catch (Exception e) {
            LOG.warn("Error during obtaining from cache: " + CacheName.MOVIES + ", key: " + rank, e);
            return Optional.empty();
        }
    }

    private void putMovieInCacheSafety(Object key, Movie movie) {
        try {
            cacheManager.getCache(CacheName.MOVIES.getValue()).put(key, movie);
        } catch (Exception e) {
            LOG.warn(String.format("Error during putting in cache: %s, key: %s, value: %s", CacheName.MOVIES, key, movie), e);
        }
    }
}
