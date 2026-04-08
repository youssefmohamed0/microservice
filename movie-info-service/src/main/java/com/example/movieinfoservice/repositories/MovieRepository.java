package com.example.movieinfoservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.movieinfoservice.models.Movie;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

}