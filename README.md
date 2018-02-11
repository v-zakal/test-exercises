# test-exercises
The service gets HTTP requests for movie info by rank in IMDb Top 250  (loaded from file), checks the Redis cache first, and serves the data found in the cache. If the cache does not have the data, it will fetch the data from the mongo database, and then update the cache.
## request/response examples

### Success (http status 200)
curl -X POST -H "Content-Type: application/json" -d '{"rank":4}' localhost:8080/getMovieByRank

{"code":200,"message":"OK","rank":4,"title":"The Dark Knight","year":2008}

### Not found (http status 422)

curl -X POST -H "Content-Type: application/json" -d '{"rank":255}' localhost:8080/getMovieByRank

{"code":422,"message":"Movie with rank 255 not found"}

### Validation error (http status 400)
curl -X POST -H "Content-Type: application/json" -d '{"rank":256}' localhost:8080/getMovieByRank

{"code":400,"message":"rank : [must be less than or equal to 255]","path":"/getMovieByRank"}
