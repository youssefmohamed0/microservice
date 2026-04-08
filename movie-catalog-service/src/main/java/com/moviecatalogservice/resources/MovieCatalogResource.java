package com.moviecatalogservice.resources;

import com.moviecatalogservice.models.CatalogItem;
import com.moviecatalogservice.models.Movie;
import com.moviecatalogservice.models.Rating;
import com.moviecatalogservice.models.UserRating;
import com.moviecatalogservice.services.MovieInfoService;
import com.moviecatalogservice.services.UserRatingService;
import com.moviecatalogservice.services.TrendingClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.GetMapping; 
import com.example.trendingmoviesservice.grpc.TrendingMoviesServiceGrpc;
import com.example.trendingmoviesservice.grpc.TrendingRequest;
import com.example.trendingmoviesservice.grpc.TrendingResponse;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;

    private final MovieInfoService movieInfoService;

    private final UserRatingService userRatingService;

    private final TrendingClientService trendingService;

    public MovieCatalogResource(RestTemplate restTemplate,
                                MovieInfoService movieInfoService,
                                UserRatingService userRatingService,
                                TrendingClientService trendingService) {

        this.restTemplate = restTemplate;
        this.movieInfoService = movieInfoService;
        this.userRatingService = userRatingService;
        this.trendingService = trendingService;
    }

    /**
     * Makes a call to MovieInfoService to get movieId, name and description,
     * Makes a call to RatingsService to get ratings
     * Accumulates both data to create a MovieCatalog
     * @param userId
     * @return CatalogItem that contains name, description and rating
     */
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable String userId) {
        List<Rating> ratings = userRatingService.getUserRating(userId).getRatings();
        return ratings.stream().map(movieInfoService::getCatalogItem).collect(Collectors.toList());
    }

    /**
     * Makes a gRPC call to TrendingMoviesService to find out top 10 movies [cite: 71]
     * @return List of top 10 movies by rating
     */
    @GetMapping("/trending") // Using GetMapping for the specific new endpoint 
    public List<CatalogItem> getTrending() {
        TrendingResponse response = trendingService.fetchTrendingMovies();
        return response.getMoviesList().stream()
            
            .map(tm -> {
                    try {
                        // Call Movie Info Service
                        Movie movie = restTemplate.getForObject(
                            "http://movie-info-service/movies/" + tm.getMovieId(), 
                            Movie.class
                        );

                        return new CatalogItem(
                            movie.getName(), 
                            movie.getDescription(), 
                            (int) tm.getAverageRating()
                        );
                    } catch (Exception e) {
                        // If movie info is not found, return a fallback so the whole list doesn't fail
                        return new CatalogItem(
                            "Unknown Movie (" + tm.getMovieId() + ")", 
                            "Details not found in Movie Info Service", 
                            (int) tm.getAverageRating()
                        );
                    }
            }).collect(Collectors.toList());
    }
}
