package com.example.pilem.ui.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.MovieEntity;
import com.example.pilem.data.model.Genre;
import com.example.pilem.data.model.MovieCreditsResponse;
import com.example.pilem.data.model.MovieDetailResponse;
import com.example.pilem.data.remote.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_POSTER = "extra_poster";

    private int movieId;
    private String movieTitle;
    private String moviePoster;
    private boolean isBookmarked = false;

    private ImageView ivBackdrop, ivPoster;
    private TextView tvTitle, tvRating, tvGenres, tvOverview;
    private Button btnBookmark;
    private RecyclerView rvCast;
    private CastAdapter castAdapter;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        setupRecyclerView();

        movieId = getIntent().getIntExtra(EXTRA_ID, 0);
        movieTitle = getIntent().getStringExtra(EXTRA_TITLE);
        moviePoster = getIntent().getStringExtra(EXTRA_POSTER);

        // Awalnya set data dari intent (fallback)
        tvTitle.setText(movieTitle);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + moviePoster).into(ivPoster);

        checkBookmarkStatus();
        loadMovieDetails();
        loadMovieCredits();

        btnBookmark.setOnClickListener(v -> toggleBookmark());
    }

    private void initViews() {
        ivBackdrop = findViewById(R.id.iv_detail_backdrop);
        ivPoster = findViewById(R.id.iv_detail_poster);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvRating = findViewById(R.id.tv_detail_rating);
        tvGenres = findViewById(R.id.tv_detail_genres);
        tvOverview = findViewById(R.id.tv_detail_overview);
        btnBookmark = findViewById(R.id.btn_bookmark);
        rvCast = findViewById(R.id.rv_cast);
        progressBar = findViewById(R.id.pb_detail);
        scrollView = findViewById(R.id.scroll_view);
    }

    private void setupRecyclerView() {
        castAdapter = new CastAdapter();
        rvCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCast.setAdapter(castAdapter);
    }

    private void loadMovieDetails() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        RetrofitClient.getApiService().getMovieDetail(movieId).enqueue(new Callback<MovieDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetailResponse> call, @NonNull Response<MovieDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayDetails(response.body());
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to load details", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetailResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMovieCredits() {
        RetrofitClient.getApiService().getMovieCredits(movieId).enqueue(new Callback<MovieCreditsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieCreditsResponse> call, @NonNull Response<MovieCreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    castAdapter.setCastList(response.body().getCast());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {
                // Ignore cast error or show small message
            }
        });
    }

    private void displayDetails(MovieDetailResponse detail) {
        tvTitle.setText(detail.getTitle());
        tvOverview.setText(detail.getOverview());
        tvRating.setText(String.format("⭐ %.1f", detail.getVoteAverage()));

        // Format Genres
        StringBuilder genres = new StringBuilder();
        List<Genre> genreList = detail.getGenres();
        if (genreList != null) {
            for (int i = 0; i < genreList.size(); i++) {
                genres.append(genreList.get(i).getName());
                if (i < genreList.size() - 1) genres.append(", ");
            }
        }
        tvGenres.setText(genres.toString());

        Glide.with(this).load("https://image.tmdb.org/t/p/w780" + detail.getBackdropPath()).into(ivBackdrop);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + detail.getPosterPath()).into(ivPoster);
        
        // Update local data
        movieTitle = detail.getTitle();
        moviePoster = detail.getPosterPath();
    }

    private void checkBookmarkStatus() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MovieEntity movie = AppDatabase.getDatabase(this).movieDao().getMovieById(movieId);
            isBookmarked = movie != null;
            runOnUiThread(() -> btnBookmark.setText(isBookmarked ? "Remove from Bookmark" : "Add to Bookmark"));
        });
    }

    private void toggleBookmark() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            MovieEntity movie = new MovieEntity(movieId, movieTitle, moviePoster);
            if (isBookmarked) {
                AppDatabase.getDatabase(this).movieDao().delete(movie);
                isBookmarked = false;
            } else {
                AppDatabase.getDatabase(this).movieDao().insert(movie);
                isBookmarked = true;
            }
            runOnUiThread(() -> {
                btnBookmark.setText(isBookmarked ? "Remove from Bookmark" : "Add to Bookmark");
                Toast.makeText(this, isBookmarked ? "Added to Bookmark" : "Removed from Bookmark", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
