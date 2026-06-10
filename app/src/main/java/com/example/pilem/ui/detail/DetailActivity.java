package com.example.pilem.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.MovieEntity;
import com.example.pilem.data.local.UserSession;
import com.example.pilem.data.model.Genre;
import com.example.pilem.data.model.MovieCreditsResponse;
import com.example.pilem.data.model.MovieDetailResponse;
import com.example.pilem.data.model.Video;
import com.example.pilem.data.model.VideoResponse;
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
    private boolean isWatchlist = false;
    private UserSession userSession;

    private ImageView ivBackdrop, ivPoster;
    private TextView tvTitle, tvRating, tvGenres, tvOverview;
    private Button btnWatchlist, btnWatchTrailer;
    private ImageButton btnBack;
    private RecyclerView rvCast;
    private CastAdapter castAdapter;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        userSession = new UserSession(this);

        initViews();
        setupRecyclerView();

        movieId = getIntent().getIntExtra(EXTRA_ID, 0);
        movieTitle = getIntent().getStringExtra(EXTRA_TITLE);
        moviePoster = getIntent().getStringExtra(EXTRA_POSTER);

        tvTitle.setText(movieTitle);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + moviePoster).into(ivPoster);

        checkWatchlistStatus();
        loadMovieDetails();
        loadMovieCredits();
        loadMovieTrailer();

        btnWatchlist.setOnClickListener(v -> toggleWatchlist());
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        ivBackdrop = findViewById(R.id.iv_detail_backdrop);
        ivPoster = findViewById(R.id.iv_detail_poster);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvRating = findViewById(R.id.tv_detail_rating);
        tvGenres = findViewById(R.id.tv_detail_genres);
        tvOverview = findViewById(R.id.tv_detail_overview);
        btnWatchlist = findViewById(R.id.btn_bookmark);
        btnWatchTrailer = findViewById(R.id.btn_watch_trailer);
        btnBack = findViewById(R.id.btn_back_detail);
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
            public void onFailure(@NonNull Call<MovieCreditsResponse> call, @NonNull Throwable t) {}
        });
    }

    private void loadMovieTrailer() {
        RetrofitClient.getApiService().getMovieVideos(movieId).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> videos = response.body().getResults();
                    for (Video video : videos) {
                        if (video.getSite().equalsIgnoreCase("YouTube") && video.getType().equalsIgnoreCase("Trailer")) {
                            btnWatchTrailer.setVisibility(View.VISIBLE);
                            btnWatchTrailer.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.getKey()));
                                startActivity(intent);
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {}
        });
    }

    private void displayDetails(MovieDetailResponse detail) {
        tvTitle.setText(detail.getTitle());
        tvOverview.setText(detail.getOverview());
        tvRating.setText(String.format("⭐ %.1f", detail.getVoteAverage()));

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
        
        movieTitle = detail.getTitle();
        moviePoster = detail.getPosterPath();
    }

    private void checkWatchlistStatus() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int userId = userSession.getUserId();
            MovieEntity movie = AppDatabase.getDatabase(this).movieDao().getMovieById(movieId, userId);
            isWatchlist = movie != null;
            runOnUiThread(() -> btnWatchlist.setText(isWatchlist ? R.string.remove_from_watchlist : R.string.add_to_watchlist));
        });
    }

    private void toggleWatchlist() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            int userId = userSession.getUserId();
            MovieEntity movie = new MovieEntity(movieId, userId, movieTitle, moviePoster);
            if (isWatchlist) {
                AppDatabase.getDatabase(this).movieDao().delete(movie);
                isWatchlist = false;
            } else {
                AppDatabase.getDatabase(this).movieDao().insert(movie);
                isWatchlist = true;
            }
            runOnUiThread(() -> {
                btnWatchlist.setText(isWatchlist ? R.string.remove_from_watchlist : R.string.add_to_watchlist);
                Toast.makeText(this, isWatchlist ? "Added to Watchlist" : "Removed from Watchlist", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
