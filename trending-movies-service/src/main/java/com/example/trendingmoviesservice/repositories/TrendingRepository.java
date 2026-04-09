package com.example.trendingmoviesservice.repositories;

import com.example.trendingmoviesservice.models.Rating;
import com.example.trendingmoviesservice.models.TrendingMovieResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendingRepository extends JpaRepository<Rating, Long> {

    // @Query lets us write raw, native SQL exactly as you designed it!
    @Query(value = "SELECT movie_id AS movieId, AVG(rating) AS rating FROM user_ratings GROUP BY movie_id ORDER BY rating DESC LIMIT 10", nativeQuery = true)
    List<TrendingMovieResult> findTop10TrendingMovies();
}