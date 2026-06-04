package com.example.pilem.data.model;

import com.google.gson.annotations.SerializedName;

public class Cast {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("profile_path")
    private String profilePath;
    @SerializedName("character")
    private String character;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getProfilePath() { return profilePath; }
    public String getCharacter() { return character; }
}
