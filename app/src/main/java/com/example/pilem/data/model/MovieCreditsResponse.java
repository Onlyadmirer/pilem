package com.example.pilem.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieCreditsResponse {
    @SerializedName("cast")
    private List<Cast> cast;

    public List<Cast> getCast() { return cast; }
}
