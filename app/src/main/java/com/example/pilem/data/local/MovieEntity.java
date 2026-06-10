package com.example.pilem.data.local;

import androidx.room.Entity;

@Entity(tableName = "watchlist_movies", primaryKeys = {"id", "userId"})
public class MovieEntity {
    private int id;
    private int userId; // Relasi ke UserEntity
    private String title;
    private String posterPath;

    public MovieEntity(int id, int userId, String title, String posterPath) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.posterPath = posterPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
}
