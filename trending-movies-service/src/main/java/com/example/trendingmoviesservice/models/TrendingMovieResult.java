package com.example.trendingmoviesservice.models;

// This interface maps directly to the "AS" aliases in our SQL query!
public interface TrendingMovieResult {
    String getMovieId();
    Double getRating(); // AVG() in SQL returns a decimal
}