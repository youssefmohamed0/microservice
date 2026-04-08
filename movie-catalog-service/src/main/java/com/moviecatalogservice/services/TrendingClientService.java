package com.moviecatalogservice.services;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import com.example.trendingmoviesservice.grpc.TrendingMoviesServiceGrpc;
import com.example.trendingmoviesservice.grpc.TrendingRequest;
import com.example.trendingmoviesservice.grpc.TrendingResponse;

@Service
public class TrendingClientService {

    @GrpcClient("trendingClient")
    private TrendingMoviesServiceGrpc.TrendingMoviesServiceBlockingStub trendingStub;

    public TrendingResponse fetchTrendingMovies() {
        // // Create an empty request as defined in your .proto file
        // TrendingRequest request = TrendingRequest.newBuilder().build();
        
        // // The stub automatically asks the Discovery Server for the trending-movies-service IP
        // return trendingStub.getTop10Movies(request);
        return trendingStub.getTopTrendingMovies(TrendingRequest.newBuilder().build());
    }
}