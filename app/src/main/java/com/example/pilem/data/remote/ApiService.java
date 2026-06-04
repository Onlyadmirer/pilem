package com.example.pilem.data.remote;

import com.example.pilem.data.model.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("language") String language, @Query("page") int page);
}
