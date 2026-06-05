package com.example.pilem.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarked_movies")
public class MovieEntity {
    @PrimaryKey
    private int id;
    private String title;
    private String posterPath;

    public MovieEntity(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
}
