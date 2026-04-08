package com.example.trendingmoviesservice.services;

import com.example.trendingmoviesservice.grpc.TrendingMovie;
import com.example.trendingmoviesservice.grpc.TrendingMoviesServiceGrpc;
import com.example.trendingmoviesservice.grpc.TrendingRequest;
import com.example.trendingmoviesservice.grpc.TrendingResponse;
import com.example.trendingmoviesservice.models.TrendingMovieResult;
import com.example.trendingmoviesservice.repositories.TrendingRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

// @GrpcService tells Spring Boot to expose this over the high-speed gRPC port!
@GrpcService
public class TrendingMoviesServiceImpl extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

    private final TrendingRepository trendingRepository;

    // Inject the database repository
    public TrendingMoviesServiceImpl(TrendingRepository trendingRepository) {
        this.trendingRepository = trendingRepository;
    }

    @Override
    public void getTopTrendingMovies(TrendingRequest request, StreamObserver<TrendingResponse> responseObserver) {
        
        // 1. Fetch the raw data from MySQL
        List<TrendingMovieResult> topMoviesFromDb = trendingRepository.findTop10TrendingMovies();

        // 2. Start building the gRPC Response
        TrendingResponse.Builder responseBuilder = TrendingResponse.newBuilder();

        // 3. Loop through the DB results and convert them to the Protobuf 'TrendingMovie' objects
        for (TrendingMovieResult dbResult : topMoviesFromDb) {
            
            // We use the generated Builder to create the Protobuf object
            TrendingMovie grpcMovie = TrendingMovie.newBuilder()
                    .setMovieId(dbResult.getMovieId())
                    // We cast the Double from SQL to an int32 to match your .proto file
                    .setAverageRating((int) Math.round(dbResult.getRating())) 
                    .build();
            
            responseBuilder.addMovies(grpcMovie);
        }

        // 4. Send the final response back to the caller (The Catalog Service)
        responseObserver.onNext(responseBuilder.build());
        
        // 5. Tell gRPC we are finished sending data
        responseObserver.onCompleted();
    }
}