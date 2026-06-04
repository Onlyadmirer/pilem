package com.example.pilem.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieEntity movie);

    @Delete
    void delete(MovieEntity movie);

    @Query("SELECT * FROM favorite_movies")
    List<MovieEntity> getAllFavoriteMovies();

    @Query("SELECT * FROM favorite_movies WHERE id = :id")
    MovieEntity getMovieById(int id);
}
