package com.example.pilem.data.remote;

import com.example.pilem.data.model.MovieCreditsResponse;
import com.example.pilem.data.model.MovieDetailResponse;
import com.example.pilem.data.model.MovieResponse;
import com.example.pilem.data.model.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("language") String language, @Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("language") String language, @Query("page") int page);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("language") String language, @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDetailResponse> getMovieDetail(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("movie_id") int movieId);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(@Path("movie_id") int movieId);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("query") String query, @Query("language") String language, @Query("page") int page);
}
