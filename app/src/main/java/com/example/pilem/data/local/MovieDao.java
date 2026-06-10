package com.example.pilem.data.local;

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

    @Query("SELECT * FROM watchlist_movies WHERE userId = :userId")
    List<MovieEntity> getAllWatchlistMovies(int userId);

    @Query("SELECT * FROM watchlist_movies WHERE id = :id AND userId = :userId LIMIT 1")
    MovieEntity getMovieById(int id, int userId);
}
